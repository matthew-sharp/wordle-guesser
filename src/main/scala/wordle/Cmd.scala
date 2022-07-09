package wordle

import wordle.model.Word

sealed trait Cmd
object Cmd {
  case object Nothing extends Cmd
  final case class SetWordlist(filename: Option[String]) extends Cmd
  final case class SetAnswers(filename: Option[String]) extends Cmd
  case object SetResultMap extends Cmd
  case object AdvanceSolver extends Cmd
  final case class AskGuessMenu(choices: Map[String, Word]) extends Cmd
  case object AskResult extends Cmd
}
