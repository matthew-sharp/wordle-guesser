package wordle.io

import wordle.model.{Constraint, ConstraintType}

import scala.io.StdIn.readLine

object Terminal {
  def readGuess(candidate: String): String = {
    print(s"enter guess (blank to accept candidate '$candidate'): ")
    val input = readLine()
    if (input.isEmpty) candidate else input
  }

  def readResult(word: String): List[Constraint] = {
    print("result? ")
    val raw = readLine().zip(word)
    println()
    raw.map { case (rawType, c) =>
      val conType = rawType match {
        case 'b' => ConstraintType.Absent
        case 'y' => ConstraintType.Exists
        case 'g' => ConstraintType.Position
      }
      Constraint(c, conType)
    }.toList
  }
}
