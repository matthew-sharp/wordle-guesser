package wordle

import scala.io.Source
import scala.io.StdIn.readLine
import java.nio.file.Files
import java.nio.file.Paths

object wordle extends App {
  val wordsSource =
    if (Files.exists(Paths.get("wordlist"))) {
      Source.fromFile("wordlist")
    }
    else {
      println("wordlist not found in current directory, using built-in word list")
      Source.fromResource("wordlist")
    }
  val words = wordsSource.getLines().toSet
  wordsSource.close()

  val guesser = new WordleGuesser(words, guess => {
    print("result?: ")
    readResult(guess)
  })

  guesser.guess()

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
