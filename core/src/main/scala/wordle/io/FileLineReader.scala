package wordle.io

import cats.effect.implicits.*
import cats.effect.{IO, Resource}

import scala.io.Source

trait FileLineReader(defaultName: String) {
  def read(filename: Option[String], resourceName: Option[String]): IO[Seq[String]] = {
    val wordsSource = (filename, resourceName) match
      case (Some(f), _) => Resource.fromAutoCloseable(IO.blocking(Source.fromFile(f)))
      case (None, Some(r)) => Resource.fromAutoCloseable(IO.blocking(Source.fromResource(r)))
      case _ => Resource.raiseError[IO, Source, Throwable](
        new IllegalArgumentException("Either file name or resource name must be specified"))
    wordsSource.use(source => IO.blocking(source.getLines().toSeq))
  }

  def read(filename: Option[String]): IO[Seq[String]] = read(filename, Some(defaultName))
}
