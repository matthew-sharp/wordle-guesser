package wordle.interactive

import wordle.Cmd
import wordle.entropy.EntropyScorer
import wordle.model.Model
import wordle.update.StartSolveCommon
import wordle.util.LookupPruner

object StartInteractiveSolve {
  def apply(model: Model): (Model, Cmd) = {
    val scorer = EntropyScorer(model.resultsCache)
    val solver = InteractiveSolver(scorer = scorer, pruner = LookupPruner(model.resultsCache))
    StartSolveCommon(model, solver)
  }
}
