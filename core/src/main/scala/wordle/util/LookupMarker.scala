package wordle.util

import wordle.model.{CachedResults, ConstraintType, Word}

object LookupMarker {
  def mark(cache: CachedResults)(guess: Word, answer: Word): List[ConstraintType] = {
    val ternary = cache.getResult(guess, answer)
    ResultUtils.toConstraintTypes(ternary)
  }
}
