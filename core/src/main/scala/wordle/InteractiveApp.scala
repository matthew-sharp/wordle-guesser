package wordle

import cats.data.StateT
import cats.effect.*
import cats.implicits.*
import wordle.Msg.*
import wordle.auto.StartAutoSolve
import wordle.entropy.ResultCacheBuilder
import wordle.interactive.{InteractiveMenuParser, InteractiveSolver, InteractiveUpdate, StartInteractiveSolve}
import wordle.io.{AnswerListReader, FileLineReader, WordlistReader}
import wordle.model.*
import wordle.parser.TopLevelParser
import wordle.update.*

import scala.annotation.tailrec
import scala.collection.immutable.{BitSet, Queue}

object InteractiveApp extends IOApp {
  def quit(msg: Msg): Boolean = msg == Quit

  def init(args: List[String]): (Model[_], Cmd) = {
    @tailrec
    def parseArgs(parsed: Map[String, Any], remaining: List[String], msgs: Seq[String]): (Map[String, Any], Seq[String]) = {
      remaining match
        case Nil => (parsed, msgs)
        case ("-s" | "--solve-file") :: filename :: tail =>
          parseArgs(parsed ++ Map("solveFile" -> Some(filename)), tail, msgs)
        case ("-al" | "--answer-list") :: filename :: tail =>
          parseArgs(parsed ++ Map("answerList" -> Some(filename)), tail, msgs)
        case unknown :: tail =>
          parseArgs(parsed, tail, msgs :+ s"skipping unknown argument $unknown")
    }
    val (clConf, msgs) = parseArgs(Map(), args, Seq.empty[String])

    val defaultConf = Map(
      "solveFile" -> None,
      "answerList" -> None,
    )

    val conf = defaultConf ++ clConf

    val queuedAutoSolveCmd = conf("solveFile").asInstanceOf[Option[String]].map(Cmd.GetSolveTargets.apply)
    val initialCmds = Seq[Option[Cmd]](
      Some(Cmd.SetAnswers(conf("answerList").asInstanceOf[Option[String]])),
      queuedAutoSolveCmd,
    ).flatten

    (Model(
      batchMode = queuedAutoSolveCmd.isDefined,
      List(Console(
        outputMsg = msgs.mkString("\n"),
        prompt = ">",
        parseCallback = TopLevelParser.parse,
      )),
      queuedCmds = Queue.from(initialCmds),
      queuedSolves = List.empty[String],
      resultsCache = null,
      validAnswers = None,
      solver = null,
      state = SolverState.Inactive,
      currentGuess = -1,
      guessNum = 0,
      boards = Seq.empty[Board],
    ), Cmd.SetWordlist(None))
  }

  private def updateCore[T <: Solver[T]](msg: Msg, model: Model[T]): Option[(Model[_], Cmd)] = {
    msg match {
      case Quit => Some((model, Cmd.Nothing))
      case Invalid(failMsg) => Some((model.setOutputMsgIfNotBatch(s"Invalid input: $failMsg"), Cmd.Nothing))
      case SetWordlist(filename) => Some((model, Cmd.SetWordlist(filename)))
      case SetWordlistResult(words) => Some(UpdateWordlist(model, words))
      case SetResultMap(result) => Some((model.setOutputMsgIfNotBatch("Precalculated results read")
        .copy(resultsCache = result), Cmd.Nothing))
      case SetAnswerList(filename) => Some((model, Cmd.SetAnswers(filename)))
      case ClearAnswerList => Some((model.copy(validAnswers = None), Cmd.Nothing))
      case SetAnswerListResult(answers) => Some(UpdateAnswerList(model, answers))
      case AdvanceSolver => Some(AdvanceSolverUpd(model))
      case AutoSolve(answer) => Some(StartAutoSolve(model, answer))
      case QueueAutoSolveTargets(solves) => Some(model.copy(queuedSolves = model.queuedSolves ++ solves), Cmd.Nothing)
      case _ => None
    }
  }

  def update[T <: Solver[T]](msg: Msg, model: Model[T]): (Model[_], Cmd) = {
    type Handler[T <: Solver[T]] = (Msg, Model[T]) => Option[(Model[_], Cmd)]

    def firstMatch(handlers: List[Handler[T]]): Option[(Model[_], Cmd)] = {
      handlers match
        case Nil => None
        case handler :: tail => handler(msg, model).orElse(firstMatch(tail))
    }

    val handlers: List[Handler[T]] = updateCore :: {
      model match
        case _: Model[InteractiveSolver] => List[Handler[T]](InteractiveUpdate.update)
        case _ => List.empty[Handler[T]]
    }
    val (interimModel, interimCmd) = firstMatch(handlers).get
    if (interimCmd == Cmd.Nothing) interimModel.queuedCmds.dequeueOption match {
      case Some(nextCmd, stillQueuedCmds) => (interimModel.copy(queuedCmds = stillQueuedCmds), nextCmd)
      case None => (interimModel, interimCmd)
    } else {
      (interimModel, interimCmd)
    }
  }

  def io(model: Model[_], cmd: Cmd): IO[Msg] = {
    val currentConsole = model.consoles.head
    val cmdIo = cmd match {
      case Cmd.Nothing =>
        model.queuedSolves match
          case Nil =>
            if (model.batchMode) IO(Msg.Quit)
            else IO.print(s"${currentConsole.prompt} ") >> IO.readLine.map(currentConsole.parseCallback)
          case _ => IO(Msg.AutoSolve(None))
      case Cmd.AdvanceSolver => IO(AdvanceSolver)
      case Cmd.SetWordlist(filename) => WordlistReader.read(filename)
        .redeem(t => Msg.Invalid(t.toString), ws => SetWordlistResult(ws.toIndexedSeq))
      case Cmd.SetAnswers(filename) => AnswerListReader.read(filename)
        .redeem(t => Msg.Invalid(t.toString), SetAnswerListResult.apply)
      case Cmd.SetResultMap => ResultCacheBuilder.resultLookup(model.resultsCache.wordMapping).map(SetResultMap.apply)
      case Cmd.GetSolveTargets(filename) => FileLineReader().read(filename)
      .redeem(t => Msg.Invalid(t.toString), Msg.QueueAutoSolveTargets.apply)
    }
    val outMsg = currentConsole.outputMsg
    (if (outMsg.isEmpty) IO.unit else IO.println(outMsg)) >> cmdIo
  }

  def setOutputMsgEvenIfBatch[T <: Solver[T]](m: Model[T], outMsg: String): Model[T] = {
    val newTopCon = m.consoles.head.copy(outputMsg = outMsg)
    m.copy(consoles = newTopCon :: m.consoles.tail)
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val (initialModel, initialCmd) = init(args)
    val app = StateT[IO, (Model[_], Cmd), Msg] {
      case (model, cmd) =>
        io(model, cmd).map { msg =>
          val (updatedModel, newCmd) = update(msg, setOutputMsgEvenIfBatch(model, ""))
          ((updatedModel, newCmd), msg)
        }
    }
    val finalModel = app
      .iterateUntil(quit)
      .run((initialModel, initialCmd))
      .map { case ((model, _), msg) => update(msg, model)._1 }
    finalModel.map(_ => ExitCode.Success)
  }
}
