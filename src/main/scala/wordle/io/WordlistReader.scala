package wordle.io

import cats.effect.{IO, Resource}

import java.nio.file.{Files, Paths}
import scala.io.Source

object WordlistReader {
  def read(filename: String = "wordlist"): IO[Set[String]] = {
    val checkedFilename = if (filename == Nil) "" else filename
    val fileExists = IO.blocking(Files.exists(Paths.get(checkedFilename)))
    val wordsSource = fileExists.flatMap { exists =>
      if (exists)
        IO(Resource.fromAutoCloseable(IO.blocking(Source.fromFile(checkedFilename))))
      else {
        IO.println("wordlist not found in current directory, using built-in word list") >>
        IO(Resource.fromAutoCloseable(IO.blocking(Source.fromResource("wordlist"))))
      }
    }
    wordsSource.flatMap(resource =>
      resource.use(source =>
        IO(source.getLines().toSet)
      ))
  }
}
