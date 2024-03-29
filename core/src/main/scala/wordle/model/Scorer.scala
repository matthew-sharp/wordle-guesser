package wordle.model

trait FlatScorer {
  def flatScore(remainingValidWords: IArray[Word])(candidate: Word, guessNum: Int): ScoreInfo
}

trait WeightedScorer {
  def prepWeightedScoringRound(remainingValidWords: IArray[(Word, Double)]): WeightedScorer

  def weightedScore(candidate: Word, guessNum: Int): ScoreInfo
}

trait Scorer extends FlatScorer with WeightedScorer

/**
 *
 * @param probability The probability that this guess is the final answer
 * @param rawScore A number representing the raw "score" of this guess.
 *                 eg. This may be expected entropy of the guess.
 * @param estimatedRemainingEffort A number representing the estimated effort after seeing the result of this guess
 *                                 given that this guess was not the answer.
 *
 */
case class ScoreInfo(
                    probability: Double,
                    rawScore: Double,
                    estimatedRemainingEffort: Double,
                    ) {
  inline def score: Double = {
    probability + (1 - probability) * (1 + estimatedRemainingEffort)
  }
}