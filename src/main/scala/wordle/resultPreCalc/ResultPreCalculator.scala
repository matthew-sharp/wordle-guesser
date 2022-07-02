package wordle.resultPreCalc

import wordle.Marker
import wordle.model.ResultUtils

object ResultPreCalculator {
  def wordToResultByteArray(word: String, words: Iterable[String]): Iterable[Array[Byte]] = {
    words.map(w => ResultUtils.toBytes(w, Marker.markForConstraints(word, w)))
  }
}
