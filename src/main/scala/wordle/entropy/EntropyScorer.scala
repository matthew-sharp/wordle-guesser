package wordle.entropy

import wordle.model.Scorer

class EntropyScorer(resultsLookup: Map[String, Map[String, Short]]) extends Scorer {
  def memoizedLog: Int => Double = {
    val cache = collection.mutable.Map.empty[Int, Double]

    num => cache.getOrElseUpdate(num, Math.log(num))
  }
  private val log2 = memoizedLog(2)

  def score(candidate: String, validAnswers: Set[String], guessNum: Int): Double = {
    val totalGuesses = validAnswers.size
    val startingEntropy = memoizedLog(totalGuesses)
    val resultsForCandidate = resultsLookup(candidate)
    val numByResult = validAnswers.toSeq
      .map(resultsForCandidate(_))
      .groupMapReduce(identity)(_ => 1)(_ + _).values
    val possibleAnswerBias =
      if (validAnswers.contains(candidate))
        if (totalGuesses <= 1) 1
        else log2 / memoizedLog(totalGuesses)
      else 0
    (startingEntropy - numByResult.map(c => c * memoizedLog(c)).sum / totalGuesses) / log2 + possibleAnswerBias
  }
}
