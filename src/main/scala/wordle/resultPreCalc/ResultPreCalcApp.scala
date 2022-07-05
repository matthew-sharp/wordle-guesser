package wordle.resultPreCalc

import cats.syntax.parallel._
import cats.effect.{ExitCode, IO, IOApp}
import wordle.io.{PrecalcResultsWriter, WordlistReader}
import wordle.resultPreCalc.ResultPreCalculator.wordToResultByteArray
import wordle.util.WordUtils

import java.nio.ByteBuffer

object ResultPreCalcApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    for {
      words <- WordlistReader.read()
      wordsLength = words.size
      lookup = WordUtils.inverseWordMap(words)
      _ <- PrecalcResultsWriter.mappedByteBuffer(wordsLength * wordsLength).use {
        bb =>
          words.parTraverse(w => {
            val offset = wordsLength * lookup(w)
            writeWordResults(w, words, bb.slice(offset, wordsLength))
          })
      }
    } yield ExitCode.Success
  }

  private def writeWordResults(word: String, words: Seq[String], buffer: ByteBuffer): IO[Unit] = {
    val bytes = wordToResultByteArray(word, words)
    IO.blocking(buffer.put(bytes))
  }
}
