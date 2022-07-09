package wordle.io

import cats.effect.{IO, Resource}
import cats.implicits._

import java.nio.file.{Files, Paths}
import scala.io.Source

object AnswerListReader {
  def read(filename: Option[String] = Some("answer-list")): IO[Seq[String]] = {
    val wordsSource = filename match
      case Some(f) => Resource.fromAutoCloseable(IO.blocking(Source.fromFile(f)))
      case None => Resource.fromAutoCloseable(IO.blocking(Source.fromResource("answer-list")))
    wordsSource.use(source => IO(source.getLines().toSeq))
  }
}
