package wordle.interactive

import atto.*
import atto.Atto.*
import cats.syntax.all.*
import wordle.Msg
import wordle.model.Word

object InteractiveMenuParser {

  val guess: Parser[Any] = int | string("q") | string("fq") | take(5)

  def parse(choices: Map[Int, Word], invWordLookup: Map[String, Word])(input: String): Msg = {
    guess.parse(input.trim)
      .done
      .either
      .flatMap {
        case c: Int => choices.get(c) match {
          case Some(w) => Either.right(w)
          case None => Either.left(s"$c not a choice in the menu")
        }
        case "q" => Either.left("!abort")
        case "fq" => Either.left("!quit")
        case str: String => invWordLookup.get(str) match {
          case Some(w) => Either.right(w)
          case None => Either.left(s"Word \"$str\" is not in the wordlist we know")
        }
      }
      .map(w => MsgInteractive.SetGuess(w))
      .leftMap(l => l match
        case "!abort" => MsgInteractive.Abort
        case "!quit" => Msg.Quit
        case err => Msg.Invalid(err)
      )
      .merge
  }
}
