package wordle.update

import wordle.Cmd
import wordle.auto.AutoSolver
import wordle.entropy.EntropyScorer
import wordle.model.*
import wordle.util.LookupPruner

import scala.collection.immutable.BitSet

object StartAutoSolve {
  def apply(model: Model, word: String): (Model, Cmd) = {
    val solver = AutoSolver(
      answer = model.resultsCache.reverseWordMapping(word),
      scorer = EntropyScorer(model.resultsCache),
      pruner = LookupPruner(model.resultsCache),
    )
    val newModel = model.copy(
      solver = solver,
      currentlyPossibleAnswers = BitSet.fromSpecific(model.resultsCache.wordMapping.indices),
      guessNum = 1,
    )
    (newModel, Cmd.AdvanceSolver)
  }
}
