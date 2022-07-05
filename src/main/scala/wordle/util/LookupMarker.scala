package wordle.util

import wordle.model.{CachedResults, Constraint, Word}

object LookupMarker {
  def mark(cache: CachedResults)(guess: Word, answer: Word): List[Constraint] = {
    val totalWords = cache.wordMapping.size
    ResultUtils.toConstraints(
      cache.resultLookup(guess * totalWords + answer),
      cache.wordMapping(guess)
      )
  }
}
