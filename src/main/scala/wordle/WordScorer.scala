package wordle

import util.MeanExtensions.MeanExtensions

object WordScorer {
  def score(word: String, freqTable: Array[Array[Int]]): Int = {
    word.toArray.zipWithIndex.map {
      case (c, i) => freqTable(i)(c.toInt - 'a')
    }.sum
  }

  def geomeanScorer(word: String, freqTable: Array[Array[Int]]): Double = {
    word.toArray.zipWithIndex.map {
      case (c, i) => freqTable(i)(c.toInt - 'a')
    }.toSeq.logSumExp
  }
}
