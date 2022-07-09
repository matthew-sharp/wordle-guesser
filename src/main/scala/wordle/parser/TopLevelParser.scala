package wordle.parser

import atto.*
import atto.Atto.*
import cats.implicits.catsSyntaxEither
import cats.syntax.either.catsSyntaxEither
import wordle.Msg
import wordle.interactive.MsgInteractive

object TopLevelParser {

  private val quit: Parser[Msg] = (string("q") | string("quit")) >| Msg.Quit
  private val intSolve: Parser[Msg] = (string("int") | string("interactive-solve")) >| MsgInteractive.InteractiveSolve
  private val autoSolve: Parser[Msg] = (string("as") | string("auto-solve")) ~> skipWhitespace ~> take(5)
    .map(w => Msg.AutoSolve(w))
  private val ansList: Parser[Msg] = ((string("al") | string("answer-list")) ~ (skipWhitespace ~> takeText))
    .map((_, filename) => filename match
      case "" => Msg.SetAnswerList(None)
      case f => Msg.SetAnswerList(Some(f))
    )

  val top: Parser[Msg] = quit | intSolve | autoSolve | ansList

  def parse(input: String): Msg = {
    top
      .parse(input.trim)
      .done
      .either
    .leftMap(Msg.Invalid(_))
    .merge
  }
}
