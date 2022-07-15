package wordle

import wordle.model.{CachedResults, Constraint, Word}

trait Msg

object Msg {
  case object Quit extends Msg

  final case class Invalid(unknown: String) extends Msg

  final case class SetWordlist(filename: Option[String]) extends Msg

  final case class SetWordlistResult(result: IndexedSeq[String]) extends Msg

  final case class SetResultMap(result: CachedResults) extends Msg

  final case class SetAnswerList(filename: Option[String]) extends Msg

  case object ClearAnswerList extends Msg

  final case class SetAnswerListResult(validAnswers: Seq[String]) extends Msg

  case object AdvanceSolver extends Msg

  final case class AutoSolve(answer: String) extends Msg
}
