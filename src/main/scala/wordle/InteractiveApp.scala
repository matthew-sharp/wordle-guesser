package wordle

import entropy.ResultCacheBuilder
import io.{AnswerListReader, Terminal, WordlistReader}
import model.*
import Msg.*
import update.*
import cats.data.StateT
import cats.effect.*
import cats.implicits.*
import wordle.interactive.InteractiveMenuParser
import wordle.parser.TopLevelParser

import scala.collection.immutable.{BitSet, Queue}
import scala.util.{Success, Try}

object InteractiveApp extends IOApp {
  def quit(msg: Msg): Boolean = msg == Quit

  def init(): (Model, Cmd) = {
    (Model(
      List(Console(
        outputMsg = "",
        prompt = ">",
        parseCallback = TopLevelParser.parse,
      )),
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

  def update(msg: Msg, model: Model): (Model, Cmd) = {
    val (interimModel, interimCmd) = msg match {
      case Quit => (model, Cmd.Nothing)
      case Invalid(failMsg) => (model.setOutputMsg(s"Invalid input: $failMsg"), Cmd.Nothing)
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
      case SetGuess(g) => (model.copy(currentGuess = g).popConsole, Cmd.AdvanceSolver)
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
    val currentConsole = model.consoles.head
    val cmdIo = cmd match {
      case Cmd.Nothing => IO.print(s"${currentConsole.prompt} ") >> IO.readLine.map(currentConsole.parseCallback)
      case Cmd.AdvanceSolver => IO(AdvanceSolver)
      case Cmd.AskResult => Terminal.askResult(model)
      case Cmd.SetWordlist(filename) => WordlistReader.read(filename).map(ws => SetWordlistResult(ws.toIndexedSeq))
      case Cmd.SetAnswers(filename) => AnswerListReader.read(filename).map(SetAnswerListResult.apply)
      case Cmd.SetResultMap => ResultCacheBuilder.resultLookup(model.resultsCache.wordMapping).map(SetResultMap.apply)
    }
    val outMsg = currentConsole.outputMsg
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
