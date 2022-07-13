package wordle.auto

import wordle.Cmd
import wordle.entropy.EntropyScorer
import wordle.model.Model
import wordle.update.StartSolveCommon
import wordle.util.LookupPruner

object StartAutoSolve {
  def apply(model: Model, answer: String): (Model, Cmd) = {
    val solver = AutoSolver(
      answer = model.resultsCache.reverseWordMapping(answer),
      scorer = EntropyScorer(model.resultsCache),
      pruner = LookupPruner(model.resultsCache),
    )
    StartSolveCommon(model, solver, 1)
  }
}
