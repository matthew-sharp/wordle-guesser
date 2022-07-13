package wordle.entropy

import cats.effect.IO
import wordle.io.WordResultReader
import wordle.model.CachedResults
import wordle.util.WordUtils

object ResultCacheBuilder {
  def resultLookup(words: IndexedSeq[String]): IO[CachedResults] = {
    WordResultReader.readResultCache.map(bytes =>
      CachedResults(bytes, words, WordUtils.inverseWordMap(words))
    )
  }
}
