package wordle.util

import wordle.model.{Constraint, ConstraintType}

object Marker {
  def mark(guess: String, answer: String): Seq[Constraint] = {
    val constraints = markForConstraints(guess, answer)
    guess.zip(constraints).map(tup => Constraint(tup._1, tup._2))
  }

  def markForConstraints(guess: String, answer: String): Seq[ConstraintType] = {
    val countsInCorrectPosition = guess.zip(answer).foldLeft(Map[Char, Int]())(
      (acc, z) => z match {
        case (g, a) =>
          val updatedLetterFreq = acc.getOrElse(g, 0) + 1
          if (g == a) acc + (g -> updatedLetterFreq)
          else acc
      }
    )
    val answerCharFreq = answer.toCharArray.groupMapReduce(identity)(_ => 1)(_ + _)
    guess.zip(answer).foldLeft((Map[Char, Int](), List[ConstraintType]()))(
      (acc, z) => z match {
        case (g, a) =>
          val increment = if (g == a) 0 else 1
          val updatedLetterFreq = acc._1.getOrElse(g, 0) + increment
          val updatedCounts = acc._1 + (g -> updatedLetterFreq)
          val newConstraint = if (g == a) {
            ConstraintType.Position
          } else if (acc._1.getOrElse(g, 0) <
            answerCharFreq.getOrElse(g, 0) - countsInCorrectPosition.getOrElse(g, 0)) {
            ConstraintType.Exists
          } else ConstraintType.Absent
          (updatedCounts, acc._2 :+ newConstraint)
      })._2
  }
}
