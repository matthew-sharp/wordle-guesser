package wordle

import scala.io.Source
import io.Terminal
import util.WordPruner

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

  val guesser = new WordleGuesser(
    words,
    WordScorer.score,
    WordPruner.pruneWords,
    Terminal.readGuess,
    Terminal.readResult,
  )

  guesser.guess()

}
