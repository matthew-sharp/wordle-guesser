package wordle

import model.Scorer
import util.FrequencyCalculator
import util.MeanExtensions.MeanExtensions

object WordScorer extends Scorer {
  def legacyScore(word: String, freqTable: Array[Array[Int]]): Int = {
    word.toArray.zipWithIndex.map {
      case (c, i) => freqTable(i)(c.toInt - 'a')
    }.sum
  }

  def score(word: String,
                    currentlyValidWords: Set[String],
            guessNum: Int
                   ): Double = {
    val freqTable = FrequencyCalculator.calc(currentlyValidWords)
    val letterFreqTable = FrequencyCalculator.calcLetterFreq(freqTable)
    val letterPositionScore = word.toArray.zipWithIndex.map {
      case (c, i) => freqTable(i)(c.toInt - 'a')
    }.toSeq.logSum
    val letterScore = word.toArray.map(c => letterFreqTable(c.toInt - 'a')).toSeq.logSum
    letterPositionScore + letterScore / 2 * avoidDoubleFactor(guessNum, word)
  }

  def avoidDoubleFactor(guessNum: Int, candidate: String): Double = {
    val guessFactor = Array[Double](1.5, 1.35, 1.2, 1.1, 1, 1, 1, 1, 1, 1).apply(guessNum - 1)
    if(guessFactor < 1.01) 1
    else {
      val uniqueLettersInWord = candidate.toSet.size
      Math.log(uniqueLettersInWord * guessFactor)
    }
  }
}
