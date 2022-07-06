package wordle.update

import wordle.Cmd
import wordle.entropy.EntropyScorer
import wordle.interactive.InteractiveSolver
import wordle.model.Model
import wordle.util.LookupPruner

import scala.collection.immutable.BitSet

object StartInteractiveSolve {
  def apply(model: Model): (Model, Cmd) = {
    val scorer = EntropyScorer(model.resultsCache)
    val solver = InteractiveSolver(scorer = scorer, pruner = LookupPruner(model.resultsCache))
    StartSolveCommon(model, solver)
  }
}
