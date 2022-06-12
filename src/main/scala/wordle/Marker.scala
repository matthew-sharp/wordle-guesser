package wordle

import model.{Constraint, ConstraintType}

object Marker {
  def mark(guess: String, answer: String): List[Constraint] = {
    val countsInCorrectPosition = guess.zip(answer).foldLeft(Map[Char, Int]())(
      (acc, z) => z match {
        case (g, a) =>
          val updatedLetterFreq = acc.getOrElse(g, 0) + 1
          if (g == a) acc + (g -> updatedLetterFreq)
          else acc
      }
    )
    val answerCharFreq = answer.toCharArray.groupMapReduce(identity)(_ => 1)(_ + _)
    val res = guess.zip(answer).foldLeft((Map[Char, Int](), List[Constraint]()))(
      (acc, z) => z match {
        case(g, a) =>
          val increment = if(g == a) 0 else 1
          val updatedLetterFreq = acc._1.getOrElse(g, 0) + increment
          val updatedCounts = acc._1 + (g -> updatedLetterFreq)
          val newConstraint = if (g == a) {
            Constraint(g, ConstraintType.Position)
          } else if (acc._1.getOrElse(g, 0) <
            answerCharFreq.getOrElse(g, 0) - countsInCorrectPosition.getOrElse(g, 0)) {
            Constraint(g, ConstraintType.Exists)
          } else Constraint(g, ConstraintType.Absent)
          (updatedCounts, acc._2 :+ newConstraint)
      })
      res._2
  }
}
