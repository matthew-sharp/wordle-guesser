package wordle.entropy

import cats.effect.{ExitCode, IO, IOApp}
import wordle.WordleGuesser
import wordle.io.{Terminal, WordlistReader}
import wordle.util.WordPruner

object EntropyInteractiveApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    for {
      words <- WordlistReader.read()
      resultsLookup <- ResultMapBuilder.resultMap(words.toList)
      scorer = new EntropyScorer(resultsLookup)
      _ = new WordleGuesser(
        words = words,
        scorer = scorer,
        pruner = WordPruner.pruneWords,
        guessCallback = Terminal.readGuess,
        resultCallback = Terminal.readResult,
      ).guess()
    } yield ExitCode.Success
  }
}
