package wordle.model

case class CachedResults(
                          resultLookup: IArray[ResultTernary],
                          wordMapping: IndexedSeq[String],
                          reverseWordMapping: Map[String, Word],
                        )
