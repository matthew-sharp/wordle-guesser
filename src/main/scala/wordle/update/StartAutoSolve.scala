package wordle.update

import wordle.auto.AutoSolver
import wordle.entropy.EntropyScorer
import wordle.model._
import wordle.util.LookupPruner
import scala.collection.immutable.BitSet

object StartAutoSolve {
  def apply(model: Model, word: String): (Model, Cmd) = {
    val solver = AutoSolver(
      answer = model.resultsCache.reverseWordMapping(word),
      scorer = new EntropyScorer(model.resultsCache),
      pruner = LookupPruner(model.resultsCache),
    )
    val newModel = model.copy(
      solver = solver,
      state = SolverState.Inactive,
      currentlyPossibleAnswers = BitSet.fromSpecific(model.resultsCache.wordMapping.indices),
      guessNum = 1,
    )
    (newModel, Cmd.AdvanceSolver)
  }
}
