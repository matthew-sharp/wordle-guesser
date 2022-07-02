package wordle.model

trait Scorer {
  def score(candidate: String, remainingValidWords: Set[String], guessNum: Int): Double
}
