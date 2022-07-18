package wordle.model

case class CachedResults(
                          private val resultLookup: IArray[ResultTernary],
                          wordMapping: IndexedSeq[String],
                          reverseWordMapping: Map[String, Word],
                        )
{
  private val arrayDim = wordMapping.size

  inline def getResult(guess: Word, answer: Word): ResultTernary = {
    resultLookup(guess * arrayDim + answer)
  }

  inline def getResultsSlice(guess: Word): IArray[ResultTernary] = {
    resultLookup.slice(guess * arrayDim, guess * arrayDim + arrayDim)
  }
}
