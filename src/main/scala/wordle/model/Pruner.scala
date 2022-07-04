package wordle.model

trait Pruner {
  def pruneWords(words: Iterable[String], constraints: Seq[Constraint]): Set[String]
}
