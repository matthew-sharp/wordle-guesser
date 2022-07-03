package wordle.entropy

import cats.effect.{ExitCode, IO, IOApp}
import wordle.WordleGuesser
import wordle.driver.MenuDriver
import wordle.io.{AnswerListReader, Terminal, WordlistReader}
import wordle.util.WordPruner

import scala.annotation.tailrec

object EntropyInteractiveApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val defaultConf: Map[String, Any] = Map(
      "ask-guess-word" -> false
    )

    @tailrec
    def parseArgs(parsed: Map[String, Any], remaining: List[String]): Map[String, Any] = {
      remaining match {
        case Nil => parsed
        case ("--ask-guess-word" | "-a") :: tail =>
          parseArgs(parsed ++ Map("ask-guess-word" -> true), tail)
        case unknown :: tail =>
          println(s"skipping unknown argument $unknown")
          parseArgs(parsed, tail)
      }
    }
    val clConf = parseArgs(Map(), args)
    val conf = defaultConf ++ clConf

    val guessCallback: String => String =
      if (conf("ask-guess-word") == true) Terminal.readGuess
      else Terminal.printGuess

    for {
      words <- WordlistReader.read()
      resultsLookup <- ResultMapBuilder.resultMap(words.toList)
      answerWords <- AnswerListReader.read()
      scorer = new EntropyScorer(resultsLookup)
      _ = new MenuDriver(
        words = words,
        scorer = scorer,
        pruner = WordPruner.pruneWords,
        resultCallback = Terminal.readResult,
        answerWords
      ).go(false)
    } yield ExitCode.Success
  }
}
