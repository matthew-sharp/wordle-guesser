package wordle.interactive

import wordle.Cmd
import wordle.entropy.EntropyScorer
import wordle.model.Model
import wordle.update.StartSolveCommon
import wordle.util.LookupPruner

import scala.collection.immutable.BitSet

object StartInteractiveSolve {
  def apply(model: Model[_], numBoards: Int): (Model[InteractiveSolver], Cmd) = {
    val scorer = EntropyScorer(model.resultsCache)
    val pruner = LookupPruner(model.resultsCache)
    val solver = InteractiveSolver(scorer = scorer, pruner = pruner)
    StartSolveCommon(model, solver, numBoards)
  }
}
