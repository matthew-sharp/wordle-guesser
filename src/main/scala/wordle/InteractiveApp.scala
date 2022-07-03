package wordle

import model.Model
import cats.data.StateT
import cats.effect._
import cats.implicits._

import scala.util.Try

object InteractiveApp extends IOApp {
  sealed trait Msg
  case object Quit extends Msg
  final case class SetWordlist(filename: String) extends Msg
  final case class SetWordlistResult(result: Try[Unit]) extends Msg

  sealed trait Cmd
  object Cmd {
    case object Empty extends Cmd
    final case class SetWordlist(filename: String) extends Cmd
  }

  def quit(msg: Msg): Boolean = msg == Quit

  def init(): (Model, Cmd) = {
    (Model(
      Set.empty[String],
      Map.empty[String, Map[String, Short]],
    ), Cmd.Empty)
  }

  def update(msg: Msg, model: Model): (Model, Cmd) =
    msg match {
      case Quit => (model, Cmd.Empty)
      case SetWordlist(filename) => (model, Cmd.SetWordlist(filename))
    }

  def parse(input: String): Msg = Quit

  def io(model: Model, cmd: Cmd): IO[Msg] =
    cmd match {
      case Cmd.Empty => IO.print("> ") >> IO.readLine.map(parse)
    }

  override def run(args: List[String]): IO[ExitCode] = {
    val app = StateT[IO, (Model, Cmd), Msg] {
      case (model, cmd) =>
        io(model, cmd).map { msg =>
          val (updatedModel, newCmd) = update(msg, model)
          ((updatedModel, newCmd), msg)
        }
    }
    val (initialModel, initialCmd) = init()
    val finalModel = for {
      ((model, _), msg) <- app.iterateUntil(quit).run((initialModel, initialCmd))
    } yield update(msg, model)._1

    finalModel.map(_ => ExitCode.Success)
  }
}
