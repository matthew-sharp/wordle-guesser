package wordle.io

import cats.effect.IO
import wordle.Msg
import wordle.model.{Constraint, ConstraintType, Model}

import scala.io.StdIn.readLine

object Terminal {
  def askResult(model: Model): IO[Msg] = for
    input <- IO.readLine
    result = input.zip(model.resultsCache.wordMapping(model.currentGuess)).map {
      case(i, c) =>
        val conType = i match {
          case 'b' => ConstraintType.Absent
          case 'y' => ConstraintType.Exists
          case 'g' => ConstraintType.Position
        }
        Constraint(c, conType)
    }.toList
  yield Msg.SetResult(result)
}
