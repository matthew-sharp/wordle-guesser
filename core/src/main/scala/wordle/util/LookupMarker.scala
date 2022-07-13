package wordle.util

import wordle.model.{CachedResults, ConstraintType, Word}

object LookupMarker {
  def mark(cache: CachedResults)(guess: Word, answer: Word): List[ConstraintType] = {
    val totalWords = cache.wordMapping.size

    val ternary = cache.resultLookup(guess * totalWords + answer)
    ResultUtils.toConstraintTypes(ternary)
  }
}
