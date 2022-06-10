package wordle

object WordPrunerSafe {
  def pruneWords(words: Set[String], constraints: List[Constraint]): Set[String] = {
    val guess = constraints.map(_.c).mkString
    words.filter(Marker.mark(guess, _) == constraints)
  }
}
