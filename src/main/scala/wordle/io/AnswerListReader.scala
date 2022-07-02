package wordle.io

import cats.effect.IO

import java.nio.file.{Files, Paths}
import scala.io.Source

object AnswerListReader {
  def read(): IO[Option[Set[String]]] = {
    IO {
        if (Files.exists(Paths.get("answer-list"))) {
          val source = Source.fromFile("answer-list")
          val words = source.getLines().toSet
          source.close()
          Some(words)
        } else None
    }
  }
}
