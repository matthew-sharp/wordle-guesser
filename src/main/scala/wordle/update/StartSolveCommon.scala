package wordle.update

import wordle.Cmd
import wordle.model.{Model, Solver}

import scala.collection.immutable.BitSet

object StartSolveCommon {
  def apply(model: Model, solver: Solver): (Model, Cmd) = {
    (model.copy(
      solver = solver,
      currentlyPossibleAnswers = BitSet.fromSpecific(model.resultsCache.wordMapping.indices),
      guessNum = 1), Cmd.AdvanceSolver)
  }
}
