package wordle.interactive

import wordle.model.Word
import atto.Atto.*
import atto.*
import ParseResult.*
import cats.syntax.all.*
import wordle.Msg

object InteractiveMenuParser {

  val guess = int | take(5)

  def parse(input: String, choices: Map[Int, Word], invWordLookup: Map[String, Word]): Msg = {
    guess.parse(input.trim)
      .done
      .either
      .flatMap {
        case c: Int => choices.get(c) match {
          case Some(w) => Either.right(w)
          case None => Either.left(s"$c not a choice in the menu")
        }
        case str: String => invWordLookup.get(str) match {
          case Some(w) => Either.right(w)
          case None => Either.left(s"Word \"$str\" is not in the wordlist we know")
        }
      }
      .map(w => Msg.SetGuess(w))
      .leftMap(Msg.Invalid(_))
      .merge
  }
}
