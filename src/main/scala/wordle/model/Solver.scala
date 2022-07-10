package wordle.model

import wordle.Cmd
import wordle.util.ResultUtils

trait Solver(pruner: Pruner) {
  
  def preStats(model: Model): String = {
    if (model.boards.size == 1) s"${model.boards.head.currentlyPossibleAnswers.size} possible words remaining"
    else ""
  }

  def prepGuesses(model: Model): (Model, Cmd)

  def mark(model: Model): (Model, Cmd)

  def prune(model: Model): Model = {
    model.copy(
      state = SolverState.PreStats,
      guessNum = model.guessNum + 1,
    ).pruneBoards(pruner)
  }
  
  def solved(model: Model): String = ""
}
