package wordle.io

import wordle.model.{Constraint, ConstraintType}

import scala.io.StdIn.readLine

object Terminal {
  def readGuess(candidate: String): String = {
    print(s"enter guess (blank to accept candidate '$candidate'): ")
    val input = readLine()
    if (input.isEmpty) candidate else input
  }

  def readGuessMenu(menu: Seq[((String, Double, String), Int)]): String = {
    println("10 best words:")
    menu.foreach(i =>
      println(s"${i._2}.\t${i._1._1}${i._1._3}\t${i._1._2}")
    )
    print("selection?: ")
    val input = readLine().toInt
    val selectedItem = menu.find(i => i._2 == input).get
    selectedItem._1._1
  }

  def printGuess(candidate: String): String = {
    println(s"selecting candidate $candidate")
    candidate
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
