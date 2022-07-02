package wordle.entropy

import wordle.model.Scorer

class EntropyScorer(resultsLookup: Map[String, Map[String, Short]]) extends Scorer {
  private val log2 = Math.log(2)

  def score(candidate: String, validAnswers: Set[String], guessNum: Int): Double = {
    val totalGuesses = validAnswers.size
    val resultsForCandidate = resultsLookup(candidate)
    val numByResult = validAnswers.toSeq.map(
      resultsForCandidate(_)).groupBy(identity).values.map(_.size)
    val possibleAnswerBias = if (validAnswers.contains(candidate)) 0.5 else 0
    numByResult.map(c => c * Math.log(c)).sum / totalGuesses / log2 - possibleAnswerBias
  }
}
