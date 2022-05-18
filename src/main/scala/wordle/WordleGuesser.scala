package wordle

class WordleGuesser(
                     words: Set[String],
                     resultCallback: String => List[Constraint],
                     guessCallback: String => String) {
  val rows = 5
  val cols = 26

  private var currentlyValidWords: Set[String] = words
  private var cons: List[Constraint] = List[Constraint]()

  def guess(): (Int, String) = {
    var guessWord = ""
    var guessNum = 0
    do {
      guessNum += 1
      println(s"${currentlyValidWords.size} possible words")
      val freqTable = FrequencyCalculator.calc(currentlyValidWords)
      val letterFreq = FrequencyCalculator.calcLetterFreq(freqTable)
      val candidateWord = currentlyValidWords.maxBy { candidate =>
          WordScorer.geomeanScorer(candidate, freqTable, letterFreq) * avoidDoubleFactor(guessNum, candidate)
      }
      guessWord = guessCallback(candidateWord)
      cons = resultCallback(guessWord)
      currentlyValidWords = WordPruner.pruneWords(currentlyValidWords, cons)
    } while (!cons.forall(_.constraintType == ConstraintType.Position))
    (guessNum, guessWord)
  }

  def avoidDoubleFactor(guessNum: Int, candidate: String): Double = {
    val guessFactor = Array[Double](1.5, 1.35, 1.2, 1.1, 1, 1, 1, 1, 1, 1).apply(guessNum - 1)
    if(guessFactor < 1.01) 1
    else {
      val uniqueLettersInWord = candidate.toSet.size
      Math.log(uniqueLettersInWord * guessFactor)
    }
  }
}
