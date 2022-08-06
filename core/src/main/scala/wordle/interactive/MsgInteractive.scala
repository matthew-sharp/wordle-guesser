package wordle.interactive

import wordle.Msg
import wordle.model.{ConstraintType, Word}

object MsgInteractive {
  final case class InteractiveSolve(numBoards: Int) extends Msg

  final case class SetGuess(g: Word) extends Msg

  final case class SetResult(result: List[ConstraintType], boardNum: Int) extends Msg

  final case class ScoreSingleWord(word: Word) extends Msg
  
  case object Abort extends Msg
}
