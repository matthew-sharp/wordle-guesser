package wordle.resultPreCalc

import wordle.model.ResultTernary
import wordle.util.{Marker, ResultUtils}

object ResultPreCalculator {
  def wordToResultByteArray(word: String, words: Seq[String]): Array[ResultTernary] = {
    words.map(w => ResultUtils.toTernary(Marker.markForConstraints(word, w))).toArray
  }
}
