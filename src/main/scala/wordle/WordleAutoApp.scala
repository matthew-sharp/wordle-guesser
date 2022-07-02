package wordle

import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp}
import io.{AnswerListReader, WordlistReader}
import entropy.{EntropyScorer, ResultMapBuilder}

object WordleAutoApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val out = for {
      words <- WordlistReader.read()
      answerWords <- AnswerListReader.read()
      resultsLookup <- ResultMapBuilder.resultMap(words.toList)
    } yield (new EntropyScorer(resultsLookup), words, answerWords)
    val (scorer, words, answerWords) = out.unsafeRunSync()
    val guesser = new LoopingGuesser()

    guesser.loop(words, scorer, answerWords)
    IO(ExitCode.Success)
  }
}
