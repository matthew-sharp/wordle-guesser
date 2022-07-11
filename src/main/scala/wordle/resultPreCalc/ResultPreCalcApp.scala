package wordle.resultPreCalc

import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.parallel.*
import wordle.io.{PrecalcResultsWriter, WordlistReader}
import wordle.resultPreCalc.ResultPreCalculator.wordToResultByteArray
import wordle.util.WordUtils

import java.nio.ByteBuffer

object ResultPreCalcApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    for {
      words <- WordlistReader.read(None)
      wordsLength = words.size
      lookup = WordUtils.inverseWordMap(words)
      bb = ByteBuffer.allocate(wordsLength * wordsLength)
      _ <- IO.println("Starting generation of raw byte matrix") >>
        words.parTraverse(w => {
          val offset = wordsLength * lookup(w)
          writeWordResults(w, words, bb.slice(offset, wordsLength))
        }) >>
        IO.println("Compressing and writing byte matrix") >>
        PrecalcResultsWriter.compressWriteBytes(bb.array())
    } yield ExitCode.Success
  }

  private def writeWordResults(word: String, words: Seq[String], buffer: ByteBuffer): IO[Unit] = {
    val bytes = wordToResultByteArray(word, words)
    IO(buffer.put(bytes))
  }
}
