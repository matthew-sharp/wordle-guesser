package wordle

import scala.io.Source
import scala.io.StdIn.readLine
import java.nio.file.Files
import java.nio.file.Paths

object wordle extends App {
  val rows = 5
  val cols = 26

  val wordsSource = 
    if (Files.exists(Paths.get("wordlist"))) {
      Source.fromFile("wordlist")
    }
    else {
      println("wordlist not found in current directory, using built-in word list")
      Source.fromResource("wordlist")
    }
  var words = wordsSource.getLines().toSet
  wordsSource.close()
  var freqTable = Array.ofDim[Int](rows, cols)

  while(true) {
    println(s"${words.size} possible words")
    print("selecting candidate: ")
    freqTable = FrequencyCalculator.calc(words)
    val candidateWord = words.maxBy(WordScorer.score(_, freqTable))
    println(candidateWord)
    print("result?: ")
    val cons = readResult(candidateWord)
    words = WordPruner.pruneWords(words, cons)
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
}
