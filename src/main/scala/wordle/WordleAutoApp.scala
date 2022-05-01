package wordle

import scala.io.Source
import java.nio.file.Files
import java.nio.file.Paths

object WordleAutoApp extends App {
  val answer = args(0)

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

  val guesser = new WordleGuesser(
    words = words, resultCallback = guess => Marker.mark(guess, answer))

  val (numGuesses, _) = guesser.guess()
  println(s"answer \"$answer\" found in $numGuesses guesses")
}
