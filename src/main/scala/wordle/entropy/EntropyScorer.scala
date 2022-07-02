package wordle.entropy

import wordle.model.Scorer

class EntropyScorer(resultsLookup: Map[String, Map[String, Short]]) extends Scorer {
  private val log2 = Math.log(2)

  def score(candidate: String, validGuesses: Set[String], guessNum: Int): Double = {
    val totalGuesses = validGuesses.size
    val resultsForCandidate = resultsLookup(candidate)
    val numByResult = validGuesses.toSeq.map(
      resultsForCandidate(_)).groupBy(identity).values.map(_.size)
    val possibleAnswerBias = if (validGuesses.contains(candidate)) 0.0001 else 0
    numByResult.map(c => c * Math.log(c)).sum / totalGuesses / log2 - possibleAnswerBias
  }
}
