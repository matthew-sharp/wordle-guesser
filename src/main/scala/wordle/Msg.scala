package wordle

import wordle.model.Constraint

trait Msg

object Msg {
  final case class SetResult(result: List[Constraint]) extends Msg
}
