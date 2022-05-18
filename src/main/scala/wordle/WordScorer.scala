package wordle

import util.MeanExtensions.MeanExtensions

object WordScorer {
  def score(word: String, freqTable: Array[Array[Int]]): Int = {
    word.toArray.zipWithIndex.map {
      case (c, i) => freqTable(i)(c.toInt - 'a')
    }.sum
  }

  def geomeanScorer(word: String,
                    freqTable: Array[Array[Int]],
                    letterFreqTable: Array[Int],
                   ): Double = {
    val letterPositionScore = word.toArray.zipWithIndex.map {
      case (c, i) => freqTable(i)(c.toInt - 'a')
    }.toSeq.logSum
    val letterScore = word.toArray.map(c => letterFreqTable(c.toInt - 'a')).toSeq.logSum
    letterPositionScore + letterScore / 2
  }
}
