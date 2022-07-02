package wordle.io

import cats.effect.IO

import java.io.FileInputStream
import java.nio.file.{Files, Paths}

object WordResultReader {
  val dir = "wordle-pre-calc"
  def readWordResult(word: String): IO[Array[Byte]] = {
    IO{
      val path = Paths.get(dir, word)
      Files.readAllBytes(path)
    }
  }
}
