package wordle.model

case class CachedResults(
                       resultLookup: Array[ResultTernary],
                       wordMapping: IndexedSeq[String],
                       reverseWordMapping: Map[String, Word],
                        )
