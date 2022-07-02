package wordle.resultPreCalc

import wordle.model.ResultUtils
import wordle.util.Marker

object ResultPreCalculator {
  def wordToResultByteArray(word: String, words: Iterable[String]): Iterable[Array[Byte]] = {
    words.map(w => ResultUtils.toBytes(w, Marker.markForConstraints(word, w)))
  }
}
