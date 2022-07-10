package wordle.interactive

import wordle.Msg
import wordle.model.{ConstraintType, Word}

object MsgInteractive {
  case object InteractiveSolve extends Msg

  final case class SetGuess(g: Word) extends Msg

  final case class SetResult(result: List[ConstraintType]) extends Msg

  case object Abort extends Msg
}
