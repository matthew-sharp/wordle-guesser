package wordle.io

import cats.effect.IO

import java.nio.file.{Files, Paths}

object PrecalcResultsWriter {
  val dir = "wordle-pre-calc"
  def write(word: String, bytes: Iterable[Array[Byte]]): IO[Unit] = {
    val allBytes = bytes.reduce((l, r) => l ++ r)
    IO {
      val path = Paths.get(dir, word)
      Files.write(path, allBytes)
    }
  }
}
