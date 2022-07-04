package wordle.model

trait Solver {
  def preStats(model: Model): String

  def prepGuesses(model: Model): Model

  def mark(model: Model): Model
}
