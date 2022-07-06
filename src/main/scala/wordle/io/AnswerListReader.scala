package wordle.io

import cats.effect.{IO, Resource}
import cats.implicits._

import java.nio.file.{Files, Paths}
import scala.io.Source

object AnswerListReader {
  def read(filename: String = "answer-list"): IO[Seq[String]] = {
    val file = Resource.fromAutoCloseable(IO.blocking(Source.fromFile(filename)))
    file.use ( source =>
      IO(source.getLines().toSeq)
    )
  }
}
