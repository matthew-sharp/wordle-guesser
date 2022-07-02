package wordle.io

import cats.effect.IO

import java.io.FileInputStream

object WordResultReader {
  def readWordResult(word: String): IO[Array[Byte]] = {
    IO{
      val fis = new FileInputStream(word)
      fis.readAllBytes()
    }
  }
}
