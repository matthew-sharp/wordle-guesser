package wordle.interactive

import atto.*
import atto.Atto.*
import cats.syntax.all.*
import wordle.Msg
import wordle.model.Word

object InteractiveMenuParser {

  val guess: Parser[Any] = int
    | (char('!') ~
    (string("qq")
      | string("q")
      ))
    | take(5)

  def parse(choices: Map[Int, Word], invWordLookup: Map[String, Word])(input: String): Msg = {
    guess.parse(input.trim)
      .done
      .either
      .flatMap {
        case c: Int => choices.get(c) match {
          case Some(w) => Either.right(MsgInteractive.SetGuess(w))
          case None => Either.left(s"$c not a choice in the menu")
        }
        case ('!', cmd) =>  cmd match {
          case "qq" => Either.right(Msg.Quit)
          case "q" => Either.right(MsgInteractive.Abort)
          case x => Either.left(s"Unknown command $x")
        }
        case str: String => invWordLookup.get(str) match {
          case Some(w) => Either.right(MsgInteractive.SetGuess(w))
          case None => Either.left(s"Word \"$str\" is not in the wordlist we know")
        }
      }
      .leftMap(Msg.Invalid.apply)
      .merge
  }
}
