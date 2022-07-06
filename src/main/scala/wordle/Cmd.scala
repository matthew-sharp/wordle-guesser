package wordle

import wordle.model.Word

sealed trait Cmd
object Cmd {
  case object Prompt extends Cmd
  final case class SetWordlist(filename: String) extends Cmd
  case object SetResultMap extends Cmd
  case object AdvanceSolver extends Cmd
  final case class AskGuessMenu(choices: Map[String, Word]) extends Cmd
  case object AskResult extends Cmd
}
