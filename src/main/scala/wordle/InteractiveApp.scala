package wordle

import entropy.ResultCacheBuilder
import io.WordlistReader
import model.*
import update.*
import cats.data.StateT
import cats.effect.*
import cats.implicits.*

import scala.collection.immutable.BitSet
import scala.util.{Success, Try}

object InteractiveApp extends IOApp {
  case object Quit extends Msg
  final case class Invalid(unknown: String) extends Msg
  final case class SetWordlist(filename: String) extends Msg
  final case class SetWordlistResult(result: Try[IndexedSeq[String]]) extends Msg
  final case class SetResultMap(result: Try[CachedResults]) extends Msg
  case object AdvanceSolver extends Msg
  final case class AutoSolve(answer: String) extends Msg
  case object InteractiveSolve extends Msg

  def quit(msg: Msg): Boolean = msg == Quit

  def init(): (Model, Cmd) = {
    (Model(
      outputMsg = "",
      resultsCache = null,
      solver = null,
      state = SolverState.Inactive,
      currentlyPossibleAnswers = BitSet(),
      guessNum = 0,
      result = List.empty[Constraint]
    ), Cmd.SetWordlist("wordlist"))
  }

  def parse(input: String): Msg = {
    val words = input.split("\\s+").toList
    words match {
      case ("q" | "quit" | "exit") :: _ => Quit
      case ("int" | "interactive-solve") :: _ => InteractiveSolve
      case ("as" | "auto-solve") :: word :: _ => AutoSolve(word)
      case ("sw" | "set-wordlist") :: filename :: Nil => SetWordlist(filename)
      case unknown :: _ => Invalid(unknown)
      case Nil => Invalid("Nil")
    }
  }

  def update(msg: Msg, model: Model): (Model, Cmd) =
    msg match {
      case Quit => (model, Cmd.Prompt)
      case Invalid(unknown) => (model.copy(outputMsg = s"Unknown command: \"$unknown\""), Cmd.Prompt)
      case SetWordlist(filename) => (model, Cmd.SetWordlist(filename))
      case SetWordlistResult(words) => UpdateWordlist(model, words.get)
      case SetResultMap(result) => (model.copy(
        outputMsg = "Precalculated results read",
        resultsCache = result.get,
      ), Cmd.Prompt)
      case AdvanceSolver => AdvanceSolverUpd(model)
      case AutoSolve(answer) => StartAutoSolve(model, answer)
    }

  def io(model: Model, cmd: Cmd): IO[Msg] = {
    val cmdIo = cmd match {
      case Cmd.Prompt => IO.print("> ") >> IO.readLine.map(parse)
      case Cmd.AdvanceSolver => IO(AdvanceSolver)
      case Cmd.SetWordlist(filename) => WordlistReader.read(filename).map(ws => SetWordlistResult(Success(ws.toIndexedSeq)))
      case Cmd.SetResultMap => ResultCacheBuilder.resultLookup(model.resultsCache.wordMapping).map(r => SetResultMap(Success(r)))
    }
    (if (model.outputMsg.isEmpty) IO.unit else IO.println(model.outputMsg)) >> cmdIo
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val (initialModel, initialCmd) = init()
    val app = StateT[IO, (Model, Cmd), Msg] {
      case (model, cmd) =>
        io(model, cmd).map { msg =>
          val (updatedModel, newCmd) = update(msg, model.copy(outputMsg = ""))
          ((updatedModel, newCmd), msg)
        }
    }
    val finalModel = app.iterateUntil(quit).run((initialModel, initialCmd)).map(
      _ match { case ((model, _), msg) => update(msg, model)._1 }
    )
    finalModel.map(_ => ExitCode.Success)
  }
}
