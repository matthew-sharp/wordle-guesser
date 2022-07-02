package wordle

import entropy.EntropyScorer
import model.{Constraint, ConstraintType}
import util.WordPruner

import scala.collection.parallel.CollectionConverters._

class WordleGuesser(
                     words: Set[String],
                     resultCallback: String => Seq[Constraint],
                     guessCallback: String => String) {
  val rows = 5
  val cols = 26

  private var currentlyValidWords: Set[String] = words
  private var cons: Seq[Constraint] = List[Constraint]()

  val scorer = new EntropyScorer

  def guess(): (Int, String) = {
    var guessWord = ""
    var guessNum = 0
    do {
      guessNum += 1
      println(s"${currentlyValidWords.size} possible words")
      //val freqTable = FrequencyCalculator.calc(currentlyValidWords)
      //val letterFreq = FrequencyCalculator.calcLetterFreq(freqTable)
      val candidateWord = currentlyValidWords.take(1000).par.minBy { candidate =>
          scorer.entropy(candidate, currentlyValidWords)
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
