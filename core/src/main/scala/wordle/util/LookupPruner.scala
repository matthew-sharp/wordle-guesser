package wordle.util

import wordle.model.*

import scala.collection.immutable.BitSet

case class LookupPruner(cache: CachedResults) extends Pruner {
  def pruneWords(words: BitSet, constraints: ResultTernary, guess: Word): BitSet = {
    val cacheSlice = cache.getResultsSlice(guess)
    BitSet.fromSpecific(cacheSlice.zipWithIndex.filter(_._1 == constraints).map(_._2)).intersect(words)
  }
}
