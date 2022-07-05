package wordle.model

trait Solver() {
  def guessCmd: Cmd = Cmd.AdvanceSolver
  def markCmd: Cmd = Cmd.AdvanceSolver
  
  def preStats(model: Model): String = {
    s"${model.currentlyPossibleAnswers.size} possible words remaining"
  }

  def prepGuesses(model: Model): Model

  def mark(model: Model): Model

  def prune(model: Model): Model
  
  def solved(model: Model): String
}
