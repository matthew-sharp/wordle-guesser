package wordle.io

import cats.effect.{IO, Resource}

import java.nio.file.{Files, Paths}
import scala.io.Source

object WordlistReader {
  def read(filename: Option[String] = Some("wordlist")): IO[Seq[String]] = {
    val wordsSource = filename match
      case Some(f) => Resource.fromAutoCloseable(IO.blocking(Source.fromFile(f)))
      case None => Resource.fromAutoCloseable(IO.blocking(Source.fromResource("wordlist")))
    wordsSource.use(source => IO(source.getLines().toSeq))
  }
}
