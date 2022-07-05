package wordle.update

import wordle.entropy.EntropyScorer
import wordle.interactive.InteractiveSolver
import wordle.model.{Cmd, Model}
import wordle.util.LookupPruner

import scala.collection.immutable.BitSet

object StartInteractiveSolve {
  def apply(model: Model): (Model, Cmd) = {
    val scorer = EntropyScorer(model.resultsCache)
    val solver = InteractiveSolver(scorer = scorer, pruner = LookupPruner(model.resultsCache))
    val newModel = model.copy(
      solver = solver,
      currentlyPossibleAnswers = BitSet.fromSpecific(model.resultsCache.wordMapping.indices),
      guessNum = 1)
    (newModel, Cmd.AdvanceSolver)
  }
}
