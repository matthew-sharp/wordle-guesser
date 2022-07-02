package wordle.io

import cats.effect.IO

import java.io.{FileOutputStream, ObjectOutputStream}

object PrecalcResultsWriter {
  def write(word: String, bytes: Iterable[Array[Byte]]): IO[Unit] = {
    val allBytes = bytes.reduce((l, r) => l ++ r)
    IO {
      val fos = new FileOutputStream(word)
      fos.write(allBytes)
      fos.close()
    }
  }
}
