package wordle.model

case class CachedResults(
                          resultLookup: IArray[ResultTernary],
                          wordMapping: IndexedSeq[String],
                          reverseWordMapping: Map[String, Word],
                        )
{
  private val arrayDim = wordMapping.size
  inline def getResult(guess: Word, answer: Word): ResultTernary = {
    resultLookup(guess * arrayDim + answer)
  }
}
