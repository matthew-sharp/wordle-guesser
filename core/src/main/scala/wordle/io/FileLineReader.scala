package wordle.io

import cats.effect.implicits.*
import cats.effect.{IO, Resource}

import java.nio.file.Files
import scala.io.{BufferedSource, Source}

class FileLineReader {
  def read(filename: String): IO[Seq[String]] = {
    val linesSource = Resource.fromAutoCloseable(
      filename match
        case "-" => IO(Source.stdin)
        case _ => IO.blocking(Source.fromFile(filename))
    )
    resourceToLines(linesSource)
  }

  protected def resourceToLines(res: Resource[IO, BufferedSource]): IO[Seq[String]] =
    res.use(source => IO.blocking(source.getLines().toSeq))
}
