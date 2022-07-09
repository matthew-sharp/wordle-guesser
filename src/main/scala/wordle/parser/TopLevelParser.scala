package wordle.parser

import wordle.Msg

import atto.Atto._
import atto._
import cats.syntax.either.catsSyntaxEither
import cats.implicits.catsSyntaxEither

object TopLevelParser {

  private val quit: Parser[Msg] = (string("q") | string("quit")) >| Msg.Quit
  private val intSolve: Parser[Msg] = (string("int") | string("interactive-solve")) >| Msg.InteractiveSolve
  private val autoSolve: Parser[Msg] = (string("as") | string("auto-solve")) ~> skipWhitespace ~> take(5)
    .map (w => Msg.AutoSolve(w))
  private val ansList: Parser[Msg] = ((string("al") | string("answer-list")) ~ (skipWhitespace ~> takeText))
    .map((_, filename) => filename match
      case "" => Msg.SetAnswerList(None)
      case f => Msg.SetAnswerList(Some(f))
    )

  val top = quit | intSolve | autoSolve | ansList

  def parse(input: String): Msg = {
    top
      .parse(input.trim)
      .done
      .either
    .leftMap(Msg.Invalid(_))
    .merge
  }
}
