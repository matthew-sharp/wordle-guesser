package wordle.update

import wordle.Cmd
import wordle.auto.AutoSolver
import wordle.entropy.EntropyScorer
import wordle.model.*
import wordle.util.LookupPruner

import scala.collection.immutable.BitSet

object StartAutoSolve {
  def apply(model: Model, answer: String): (Model, Cmd) = {
    val solver = AutoSolver(
      answer = model.resultsCache.reverseWordMapping(answer),
      scorer = EntropyScorer(model.resultsCache),
      pruner = LookupPruner(model.resultsCache),
    )
    StartSolveCommon(model, solver)
  }
}
