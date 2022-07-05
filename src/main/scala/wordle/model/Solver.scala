package wordle.model

trait Solver {
  def preStats(model: Model): String

  def prepGuesses(model: Model): Model

  val guessCmd: Cmd

  val markCmd: Cmd

  def mark(model: Model): Model

  def prune(model: Model): Model
  
  def solved(model: Model): String
}
