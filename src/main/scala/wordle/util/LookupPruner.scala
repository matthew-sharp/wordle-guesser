package wordle.util

import wordle.model._

import scala.collection.immutable.BitSet

class LookupPruner (cache: CachedResults) extends Pruner {
  def pruneWords(words: BitSet, constraints: ResultTernary, guess: Word): BitSet = {
    val totalWords = cache.wordMapping.size
    val cacheSlice = cache.resultLookup.slice(guess * totalWords, guess * totalWords + totalWords)
    BitSet.fromSpecific(cacheSlice.zipWithIndex.filter(_._1 == constraints).map(_._2)).intersect(words)
  }
}
