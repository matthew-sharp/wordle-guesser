package wordle.resultPreCalc

import cats.effect.unsafe.implicits.global
import cats.instances.list._
import cats.syntax.parallel._
import cats.effect.{ExitCode, IO, IOApp}
import wordle.io.{PrecalcResultsWriter, WordlistReader}
import wordle.resultPreCalc.ResultPreCalculator.wordToResultByteArray

object ResultPreCalcApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val words = WordlistReader.read()
    val write = words.flatMap(ws => ws.toList.parTraverse(w => writeWordFile(w, ws)))
    write.unsafeRunSync()
    IO(ExitCode.Success)
  }

  def writeWordFile(word: String, words: Iterable[String]): IO[Unit] = {
    val bytes = wordToResultByteArray(word, words)
    PrecalcResultsWriter.write(word, bytes)
  }
}
