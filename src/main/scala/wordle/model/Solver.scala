package wordle.model

trait Solver(val guessCmd: Cmd, val markCmd: Cmd) {
  def preStats(model: Model): String

  def prepGuesses(model: Model): Model

  def mark(model: Model): Model

  def prune(model: Model): Model
  
  def solved(model: Model): String
}
