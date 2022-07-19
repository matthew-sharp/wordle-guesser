package wordle.io

import cats.effect.{IO, Resource}

import scala.io.Source

abstract class FileLineReaderWithResourceDefault(defaultName: String) extends FileLineReader {
  def read(filename: Option[String], resourceName: Option[String]): IO[Seq[String]] = {
    (filename, resourceName) match
      case (Some(f), _) => read(f)
      case (None, Some(r)) => resourceToLines(Resource.fromAutoCloseable(IO.blocking(Source.fromResource(r))))
      case _ => IO.raiseError(
        new IllegalArgumentException("Either file name or resource name must be specified"))
  }

  def read(filename: Option[String]): IO[Seq[String]] = read(filename, Some(defaultName))
}
