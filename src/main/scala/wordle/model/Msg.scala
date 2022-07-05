package wordle.model

trait Msg

object Msg {
  final case class SetResult(result: List[Constraint]) extends Msg
}
