package wordle.model

import wordle.util.ResultUtils

trait Solver(pruner: Pruner) {
  
  def preStats(model: Model): String = {
    s"${model.currentlyPossibleAnswers.size} possible words remaining"
  }

  def prepGuesses(model: Model): (Model, Cmd)

  def mark(model: Model): (Model, Cmd)

  def prune(model: Model): Model = {
    model.copy(
      state = SolverState.PreStats,
      guessNum = model.guessNum + 1,
      currentlyPossibleAnswers = pruner.pruneWords(
        model.currentlyPossibleAnswers,
        ResultUtils.toTernary(model.result),
        model.currentGuess)
    )
  }
  
  def solved(model: Model): String = ""
}
