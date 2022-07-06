package wordle.io

import cats.effect.{IO, Resource}
import cats.implicits._

import java.nio.file.{Files, Paths}
import scala.io.Source

object AnswerListReader {
  def read(filename: String = "answer-list"): IO[Seq[String]] = {
    val fileExists = IO.blocking(Files.exists(Paths.get(filename)))
    val wordsSource = fileExists.flatMap { exists =>
      if (exists)
        IO(Resource.fromAutoCloseable(IO.blocking(Source.fromFile(filename))))
      else {
        IO.println("answer-list not found in current directory, using built-in list") >>
          IO(Resource.fromAutoCloseable(IO.blocking(Source.fromResource("answer-list"))))
      }
    }
    wordsSource.flatMap(resource =>
      resource.use(source =>
        IO(source.getLines().toSeq)
      ))
  }
}
