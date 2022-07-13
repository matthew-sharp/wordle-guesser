package wordle.model

import scala.collection.immutable.BitSet

trait Pruner {
  def pruneWords(words: BitSet, constraints: ResultTernary, guess: Word): BitSet
}
