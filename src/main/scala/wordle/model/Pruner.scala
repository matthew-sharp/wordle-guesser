package wordle.model

trait Pruner {
  def pruneWords(words: Set[String], constraints: Seq[Constraint]): Set[String]
}
