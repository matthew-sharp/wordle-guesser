package wordle.driver

import wordle.io.Terminal
import wordle.model._
import wordle.util.TopN
import scala.collection.parallel.CollectionConverters._

class MenuDriver(
                words: Set[String],
                scorer: Scorer,
                pruner: Pruner,
                resultCallback: String => Seq[Constraint],
                answerWords: Option[Set[String]] = None,
                ) extends Driver {
  private var currentlyValidWords: Set[String] = words
  private var cons: Seq[Constraint] = List[Constraint]()

  answerWords match {
    case Some(aw) => currentlyValidWords = currentlyValidWords.intersect(aw)
    case None =>
  }

  override def go(hardMode: Boolean): (Int, String) = {
    var guessWord = ""
    var guessNum = 0
    do {
      guessNum += 1
      println(s"${currentlyValidWords.size} possible words")
      val possibleGuesses = if (hardMode) currentlyValidWords else words
      val guessesByScore = possibleGuesses.par.map(g => (g, scorer.score(g, currentlyValidWords, guessNum))).seq.toMap
      val topWords = TopN(guessesByScore, 10)
      val menu = topWords.map(w => (
        w, guessesByScore(w), if (currentlyValidWords.contains(w)) "" else "*")).zipWithIndex
      guessWord = Terminal.readGuessMenu(menu)
      cons = resultCallback(guessWord)
      currentlyValidWords = pruner.pruneWords(currentlyValidWords, cons)
    } while (!cons.forall(_.constraintType == ConstraintType.Position))
    (guessNum, guessWord)
  }
}