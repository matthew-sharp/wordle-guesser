package wordle.model

import scala.collection.immutable.BitSet

trait FlatScorer {
  def score(remainingValidWords: BitSet)(candidate: Word, guessNum: Int): Double
}

trait WeightedScorer {
  def prepWeightedScoringRound(remainingValidWords: Map[Word, Double]): WeightedScorer

  def weightedScore(candidate: Word, guessNum: Int): Double
}

trait Scorer extends FlatScorer with WeightedScorer
