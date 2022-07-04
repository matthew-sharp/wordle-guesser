package wordle.util

import wordle.model.{Constraint, Pruner}

object WordPrunerSafe extends Pruner {
  def pruneWords(words: Iterable[String], constraints: Seq[Constraint]): Set[String] = {
    val guess = constraints.map(_.c).mkString
    words.filter(Marker.mark(guess, _) == constraints).toSet
  }
}
