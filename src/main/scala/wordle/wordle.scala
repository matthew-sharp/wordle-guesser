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

  def pruneWords(words: Set[String], constraints: List[Constraint]): Set[String] = {
    val conCharsMin = constraints.filter(_.constraintType != ConstraintType.Absent).groupMapReduce(_.c)(_ => 1)(_ + _)
    val lettersOverUpperBound = constraints.filter(_.constraintType == ConstraintType.Absent).map(_.c).toSet
    val freqCappedWords = lettersOverUpperBound.foldLeft(words){
      (acc, charToDrop) => acc.filter(_.toList.count(_ == charToDrop) <= conCharsMin.getOrElse(charToDrop, 0))
    }
    val wordsWithNecessaryDoubles = conCharsMin.foldLeft(freqCappedWords) {
      (acc, kv) => kv match {
        case(k, v) => acc.filter(_.toList.count(_ == k) >= v)
      }
    }
    val filteredWords = constraints.zipWithIndex.foldLeft(wordsWithNecessaryDoubles)
    { (acc, con) => con match {
      case (Constraint(c, ConstraintType.Position), i) => acc.filter(_.charAt(i) == c)
      case (Constraint(c, ConstraintType.Exists), i) => acc.filter(w => w.contains(c) && w.charAt(i) != c)
      case (_, _) => acc
    }}
    filteredWords
  }
}
