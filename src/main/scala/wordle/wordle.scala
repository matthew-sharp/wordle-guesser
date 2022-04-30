package wordle

import scala.io.Source
import scala.io.StdIn.readLine

object wordle extends App {
  val rows = 5
  val cols = 26

  val wordsSource = Source.fromFile("wordlist")
  var words = wordsSource.getLines().toSet
  wordsSource.close()
  var freqTable = Array.ofDim[Int](rows, cols)

  while(true) {
    println(s"${words.size} possible words")
    print("selecting candidate: ")
    freqTable = FrequencyCalculator.calc(words)
    val candidateWord = words.maxBy(WordScorer.score(_, freqTable))
    println(candidateWord)
    print("result?: ")
    val cons = readResult(candidateWord)
    words = pruneWords(words, cons)
  }

  def readResult(word: String): List[Constraint] = {
    val raw = readLine().zip(word)
    raw.map { case (rawType, c) =>
      val conType = rawType match {
        case 'b' => ConstraintType.Absent
        case 'y' => ConstraintType.Exists
        case 'g' => ConstraintType.Position
      }
      Constraint(c, conType)
    }.toList
  }

  // Non handled cases:
  // * Multiple letter guessed, more than 1 but not all present
  // * Multiple yellows in result
  def pruneWords(words: Set[String], constraints: List[Constraint]): Set[String] = {
    val conCharsMin = constraints.filter(_.constraintType != ConstraintType.Absent).groupMapReduce(_.c)(_ => 1)(_ + _)
    val lettersOverUpperBound = constraints.filter(_.constraintType == ConstraintType.Absent).map(_.c).toSet
    val freqCappedWords = lettersOverUpperBound.foldLeft(words){
      (acc, charToDrop) => acc.filter(_.toList.count(_ == charToDrop) <= conCharsMin.getOrElse(charToDrop, 0))
    }
    val filteredWords = constraints.zipWithIndex.foldLeft(freqCappedWords)
    { (acc, con) => con match {
      case (Constraint(c, ConstraintType.Position), i) => acc.filter(_.charAt(i) == c)
      case (Constraint(c, ConstraintType.Exists), i) => acc.filter(w => w.contains(c) && w.charAt(i) != c)
      case (Constraint(c, ConstraintType.Absent), _) => conCharsMin.get(c) match {
        case None => acc.filter(!_.contains(c))
        case Some(_) => acc
      }
    }}
    filteredWords
  }
}
