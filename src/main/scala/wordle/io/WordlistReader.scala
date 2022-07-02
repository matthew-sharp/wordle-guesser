package wordle.io

import cats.effect.IO

import java.nio.file.{Files, Paths}
import scala.io.Source

object WordlistReader {
  def read(): IO[Set[String]] = {
    IO {
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
      words
    }
  }
}
