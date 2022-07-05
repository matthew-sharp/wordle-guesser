package wordle.model

import scala.collection.immutable.BitSet

trait Scorer {
  def score(candidate: Word, remainingValidWords: BitSet, guessNum: Int): Double
}
