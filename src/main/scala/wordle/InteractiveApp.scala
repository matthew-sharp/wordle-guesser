package wordle

import entropy.ResultCacheBuilder
import io.{AnswerListReader, Terminal, WordlistReader}
import model.*
import update.*
import cats.data.StateT
import cats.effect.*
import cats.implicits.*

import scala.collection.immutable.{BitSet, Queue}
import scala.util.{Success, Try}

object InteractiveApp extends IOApp {
  case object Quit extends Msg
  final case class Invalid(unknown: String) extends Msg
  final case class SetWordlist(filename: Option[String]) extends Msg
  final case class SetWordlistResult(result: IndexedSeq[String]) extends Msg
  final case class SetResultMap(result: CachedResults) extends Msg
  final case class SetAnswerList(filename: Option[String]) extends Msg
  final case class SetAnswerListResult(validAnswers: Seq[String]) extends Msg
  case object AdvanceSolver extends Msg
  final case class AutoSolve(answer: String) extends Msg
  case object InteractiveSolve extends Msg
  final case class SetGuess(g: Word) extends Msg

  def quit(msg: Msg): Boolean = msg == Quit

  def init(): (Model, Cmd) = {
    (Model(
      Console(
        outputMsg = "",
        prompt = ">",
      ),
      queuedCmds = Queue[Cmd](
        Cmd.SetAnswers(None)
      ),
      resultsCache = null,
      validAnswers = None,
      solver = null,
      state = SolverState.Inactive,
      currentGuess = -1,
      currentlyPossibleAnswers = BitSet(),
      guessNum = 0,
      result = List.empty[Constraint]
    ), Cmd.SetWordlist(None))
  }

  def parse(input: String): Msg = {
    val words = input.split("\\s+").toList
    words match {
      case ("q" | "quit" | "exit") :: _ => Quit
      case ("int" | "interactive-solve") :: _ => InteractiveSolve
      case ("as" | "auto-solve") :: word :: _ => AutoSolve(word)
      case ("al" | "answer-list") :: filename :: Nil => SetAnswerList(Some(filename))
      case ("al" | "answer-list") :: Nil => SetAnswerList(None)
      case unknown :: _ => Invalid(unknown)
      case Nil => Invalid("Nil")
    }
  }

  def update(msg: Msg, model: Model): (Model, Cmd) = {
    val (interimModel, interimCmd) = msg match {
      case Quit => (model, Cmd.Nothing)
      case Invalid(unknown) => (model.setOutputMsg(s"Unknown command: \"$unknown\""), Cmd.Nothing)
      case SetWordlist(filename) => (model, Cmd.SetWordlist(filename))
      case SetWordlistResult(words) => UpdateWordlist(model, words)
      case SetResultMap(result) => (model.setOutputMsg("Precalculated results read")
        .copy(resultsCache = result), Cmd.Nothing)
      case SetAnswerList(filename) => (model, Cmd.SetAnswers(filename))
      case SetAnswerListResult(answers) => (model.setOutputMsg(s"${answers.size} answers read")
        .copy(validAnswers = Some(BitSet.fromSpecific(answers.map(model.resultsCache.reverseWordMapping)))),
        Cmd.Nothing)
      case AdvanceSolver => AdvanceSolverUpd(model)
      case AutoSolve(answer) => StartAutoSolve(model, answer)
      case InteractiveSolve => StartInteractiveSolve(model)
      case SetGuess(g) => (model.copy(currentGuess = g), Cmd.AdvanceSolver)
      case Msg.SetResult(r) => (model.copy(result = r), Cmd.AdvanceSolver)
    }
    if (interimCmd == Cmd.Nothing) interimModel.queuedCmds.dequeueOption match {
      case Some(nextCmd, stillQueuedCmds) => (interimModel.copy(queuedCmds = stillQueuedCmds), nextCmd)
      case None => (interimModel, interimCmd)
    } else {
      (interimModel, interimCmd)
    }
  }

  def io(model: Model, cmd: Cmd): IO[Msg] = {
    val cmdIo = cmd match {
      case Cmd.Nothing => IO.print(s"${model.console.prompt} ") >> IO.readLine.map(parse)
      case Cmd.AdvanceSolver => IO(AdvanceSolver)
      case Cmd.SetWordlist(filename) => WordlistReader.read(filename).map(ws => SetWordlistResult(ws.toIndexedSeq))
      case Cmd.SetAnswers(filename) => AnswerListReader.read(filename).map(al => SetAnswerListResult(al))
      case Cmd.SetResultMap => ResultCacheBuilder.resultLookup(model.resultsCache.wordMapping).map(SetResultMap.apply)
      case Cmd.AskGuessMenu(selections) => for {
        input <- IO.readLine
        word = selections(input)
      } yield SetGuess(word)
      case Cmd.AskResult => Terminal.askResult(model)
    }
    val outMsg = model.console.outputMsg
    (if (outMsg.isEmpty) IO.unit else IO.println(outMsg)) >> cmdIo
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val (initialModel, initialCmd) = init()
    val app = StateT[IO, (Model, Cmd), Msg] {
      case (model, cmd) =>
        io(model, cmd).map { msg =>
          val (updatedModel, newCmd) = update(msg, model.setOutputMsg(""))
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
