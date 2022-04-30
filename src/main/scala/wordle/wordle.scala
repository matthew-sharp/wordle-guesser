package wordle

import java.io.{FileInputStream, ObjectInputStream}
import scala.io.Source
import scala.io.StdIn.readLine

object wordle extends App {
  val rows = 5
  val cols = 26

  val wordsSource = Source.fromFile("wordlist")
  var words = wordsSource.getLines().toSet
  wordsSource.close()

  val freqTableReader = new ObjectInputStream(new FileInputStream("freq-table"))
  val freqTable = freqTableReader.readObject.asInstanceOf[Array[Array[Int]]]
  freqTableReader.close()

  val scorer = new WordScorer(freqTable)
  val validLetters = Array.ofDim[Boolean](5, 26)

  for (r <- 0 until rows) {
    for (c <- 0 until cols) {
      validLetters(r)(c) = true
    }
  }

  while(true) {
    val candidateWord = words.maxBy(scorer.score(_))
    println(candidateWord)
    print("result?: ")
    val cons = readResult(candidateWord)
    words = pruneWords(words, cons)
  }

  def readResult(word: String): List[Constraint] = {
    val raw = readLine().zip(word)
    raw.map { case (rawType, c) =>
      val conType = rawType match {
        case 'b' => ConstraintType.Absent
        case 'y' => ConstraintType.Exists
        case 'g' => ConstraintType.Position
      }
      Constraint(c, conType)
    }.toList
  }

  def pruneWords(words: Set[String], constrains: List[Constraint]): Set[String] = {
    val lettersToPrune = constrains.filter(_.constraintType == ConstraintType.Absent).map(_.c)
    words.filter {
      w => !lettersToPrune.exists(w.contains(_))
    }
  }
}
