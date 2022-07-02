package wordle

import model._

class WordleGuesser(
                     words: Set[String],
                     scorer: Scorer,
                     pruner: Pruner,
                     guessCallback: String => String,
                     resultCallback: String => Seq[Constraint],
                   ) {
  private var currentlyValidWords: Set[String] = words
  private var cons: Seq[Constraint] = List[Constraint]()

  def guess(): (Int, String) = {
    var guessWord = ""
    var guessNum = 0
    do {
      guessNum += 1
      println(s"${currentlyValidWords.size} possible words")
      val candidateWord = currentlyValidWords.minBy { candidate =>
          scorer.score(candidate, currentlyValidWords, guessNum)
      }
      guessWord = guessCallback(candidateWord)
      cons = resultCallback(guessWord)
      currentlyValidWords = pruner.pruneWords(currentlyValidWords, cons)
    } while (!cons.forall(_.constraintType == ConstraintType.Position))
    (guessNum, guessWord)
  }
}
