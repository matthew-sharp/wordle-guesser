package wordle.update

import wordle.Cmd
import wordle.model.*

import scala.collection.immutable.BitSet

object StartSolveCommon {
  def apply(model: Model, solver: Solver): (Model, Cmd) = {
    (model.copy(
      solver = solver,
      state = SolverState.Inactive,
      guessNum = 1,
      boards = Seq(Board(
        currentlyPossibleAnswers = model.validAnswers match {
          case Some(a) => a
          case None => BitSet.fromSpecific(model.resultsCache.wordMapping.indices)
        },
        result = List.empty[ConstraintType],
      )),
    ), Cmd.AdvanceSolver)
  }
}
