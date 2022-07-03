package wordle.io

import cats.effect.IO
import java.nio.file.{Files, Paths}

object WordResultReader {
  val dir = "wordle-pre-calc"
  def readWordResult(word: String): IO[Array[Byte]] = {
    IO.blocking {
      val path = Paths.get(dir, word)
      Files.readAllBytes(path)
    }
  }
}
