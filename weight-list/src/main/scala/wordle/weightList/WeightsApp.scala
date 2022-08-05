package wordle.weightList

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*
import wordle.io.AnswerListReader
import wordle.weightList.util.Sigmoid

import scala.io.Source

object WeightsApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val mid = args(0).toInt
    val gradient = args(1).toDouble
    for {
      previousAnswerLines <- AnswerListReader.read(None)
      validWords = previousAnswerLines.map(_.split("[\t ]+").head)
      freqSortedWords <- IO.blocking(Source.fromInputStream(System.in).getLines().toList)
      validFreqSortedWords = freqSortedWords.filter(validWords.toSet.contains)
      weightedWords = Sigmoid(mid, gradient)(validFreqSortedWords)
      _ <- weightedWords.toList.map((word, weight) => IO.println(s"$word\t$weight")).sequence
    } yield ExitCode.Success
  }
}