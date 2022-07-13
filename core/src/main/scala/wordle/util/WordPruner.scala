package wordle.util

import wordle.model.{Constraint, ConstraintType, Pruner}

object WordPruner {
  def pruneWords(words: Iterable[String], constraints: Seq[Constraint]): Set[String] = {
    val conCharsMin = constraints.filter(_.constraintType != ConstraintType.Absent).groupMapReduce(_.c)(_ => 1)(_ + _)
    val lettersOverUpperBound = constraints.filter(_.constraintType == ConstraintType.Absent).map(_.c).toSet
    val freqCappedWords = lettersOverUpperBound.foldLeft(words.toArray) {
      (acc, charToDrop) => acc.filter(_.toList.count(_ == charToDrop) <= conCharsMin.getOrElse(charToDrop, 0))
    }
    val wordsWithNecessaryDoubles = conCharsMin.foldLeft(freqCappedWords) {
      (acc, kv) =>
        kv match {
          case (k, v) => acc.filter(_.toList.count(_ == k) >= v)
        }
    }
    val filteredWords = constraints.zipWithIndex.foldLeft(wordsWithNecessaryDoubles) { (acc, con) =>
      con match {
        case (Constraint(c, ConstraintType.Position), i) => acc.filter(_.charAt(i) == c)
        case (Constraint(c, ConstraintType.Exists), i) => acc.filter(w => w.contains(c) && w.charAt(i) != c)
        case (_, _) => acc
      }
    }
    filteredWords.toSet
  }
}
