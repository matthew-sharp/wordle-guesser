package wordle

import entropy.ResultMapBuilder
import io.WordlistReader
import model._
import update._
import cats.data.StateT
import cats.effect._
import cats.implicits._

import scala.util.{Success, Try}

object InteractiveApp extends IOApp {
  case object Quit extends Msg
  final case class Invalid(unknown: String) extends Msg
  final case class SetWordlist(filename: String) extends Msg
  final case class SetWordlistResult(result: Try[Set[String]]) extends Msg
  final case class SetResultMap(result: Try[Map[String, Map[String, Short]]]) extends Msg
  final case object AdvanceSolver extends Msg
  final case class AutoSolve(answer: String) extends Msg
  case object InteractiveSolve extends Msg

  def quit(msg: Msg): Boolean = msg == Quit

  def init(): (Model, Cmd) = {
    (Model(
      outputMsg = "",
      wordlist = Set.empty[String],
      resultMap = Map.empty[String, Map[String, Short]],
      solver = null,
      state = SolverState.Inactive,
      currentlyPossibleAnswers = Set.empty[String],
      guessNum = 0,
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
        resultMap = result.get,
      ), Cmd.Prompt)
      case AdvanceSolver => AdvanceSolverUpd(model)
      case AutoSolve(answer) => StartAutoSolve(model, answer)
    }

  def io(model: Model, cmd: Cmd): IO[Msg] = {
    val cmdIo = cmd match {
      case Cmd.Prompt => IO.print("> ") >> IO.readLine.map(parse)
      case Cmd.AdvanceSolver => IO(AdvanceSolver)
      case Cmd.SetWordlist(filename) => WordlistReader.read(filename).map(ws => SetWordlistResult(Success(ws)))
      case Cmd.SetResultMap => ResultMapBuilder.resultMap(model.wordlist.toList).map(r => SetResultMap(Success(r)))
    }
    IO.println(model.outputMsg) >> cmdIo
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
    val finalModel = for {
      ((model, _), msg) <- app.iterateUntil(quit).run((initialModel, initialCmd))
    } yield update(msg, model)._1

    finalModel.map(_ => ExitCode.Success)
  }
}
