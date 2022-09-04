package wordle.weightList

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*
import wordle.io.WordlistReader
import wordle.weightList.util.{Sigmoid, SortedAnswerListReader}

import scala.io.Source

object WeightsApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val mid = args(0).toInt
    val gradient = args(1).toDouble
    for {
      sortedAnswerLines <- SortedAnswerListReader.read(None)
      validWords <- WordlistReader.read(None)
      validWordsSet = validWords.toSet
      validFreqSortedWords = sortedAnswerLines.filter(validWordsSet.contains)
      leftoverWordWeights = validWordsSet.diff(sortedAnswerLines.toSet).toSeq.sorted.map(w => (w, Double.MinPositiveValue)).toSeq
      weightedWords = Sigmoid(mid, gradient)(validFreqSortedWords)
      _ <- (leftoverWordWeights ++ weightedWords).toList.map((word, weight) => IO.println(s"$word\t$weight")).sequence
    } yield ExitCode.Success
  }
}