package wordle.entropy

import wordle.model.{CachedResults, Scorer, Word}

import scala.collection.immutable.BitSet

class EntropyScorer(resultsCache: CachedResults) extends Scorer {
  def memoizedLog: Int => Double = {
    val cache = collection.mutable.Map.empty[Int, Double]

    num => cache.getOrElseUpdate(num, Math.log(num))
  }
  private val log2 = memoizedLog(2)
  val totalWordCount = resultsCache.wordMapping.size

  def score(candidate: Word, validAnswers: BitSet, guessNum: Int): Double = {
    val totalGuesses = validAnswers.size
    val startingEntropy = memoizedLog(totalGuesses)
    val numByResult = validAnswers.toSeq
      .map(wordId => resultsCache.resultLookup(candidate * totalWordCount + wordId))
      .groupMapReduce(identity)(_ => 1)(_ + _).values
    val possibleAnswerBias =
      if (validAnswers.contains(candidate))
        if (totalGuesses <= 1) 1
        else log2 / memoizedLog(totalGuesses)
      else 0
    (startingEntropy - numByResult.map(c => c * memoizedLog(c)).sum / totalGuesses) / log2 + possibleAnswerBias
  }
}
