package wordle.model

sealed trait Cmd
object Cmd {
  case object Empty extends Cmd
  final case class SetWordlist(filename: String) extends Cmd
  final case object SetResultMap extends Cmd
}
