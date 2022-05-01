package wordle

class WordleGuesser(words: Set[String], resultCallback: String => List[Constraint]) {
  val rows = 5
  val cols = 26

  var currentlyValidWords: Set[String] = words

  var cons: List[Constraint] = List[Constraint]()

  def guess(): (Int, String) = {
    var candidateWord = ""
    var guessNum = 0
    do {
      guessNum += 1
      println(s"${currentlyValidWords.size} possible words")
      print("selecting candidate: ")
      val freqTable = FrequencyCalculator.calc(currentlyValidWords)
      candidateWord = currentlyValidWords.maxBy(WordScorer.geomeanScorer(_, freqTable))
      println(candidateWord)
      cons = resultCallback(candidateWord)
      currentlyValidWords = WordPruner.pruneWords(currentlyValidWords, cons)
    } while (!cons.forall(_.constraintType == ConstraintType.Position))
    (guessNum, candidateWord)
  }
}
