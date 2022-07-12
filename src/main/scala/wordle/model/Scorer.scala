package wordle.model

import scala.collection.immutable.BitSet

trait Scorer {
  def score(candidate: Word, remainingValidWords: BitSet, guessNum: Int): Double
}

trait WeightedScorer {
  def prepWeightedScoringRound(remainingValidWords: Map[Word, Double]): WeightedScorer

  def weightedScore(candidate: Word, guessNum: Int): Double
}
