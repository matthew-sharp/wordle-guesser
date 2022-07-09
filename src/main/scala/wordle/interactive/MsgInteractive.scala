package wordle.interactive

import wordle.Msg
import wordle.model.{Constraint, Word}

object MsgInteractive {
  case object InteractiveSolve extends Msg

  final case class SetGuess(g: Word) extends Msg

  final case class SetResult(result: List[Constraint]) extends Msg
}
