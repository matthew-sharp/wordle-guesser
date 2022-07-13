package wordle.update

import wordle.Cmd
import wordle.model.*

import scala.collection.immutable.BitSet

object StartSolveCommon {
  def apply(model: Model, solver: Solver, numBoards: Int): (Model, Cmd) = {
    (model.copy(
      solver = solver,
      state = SolverState.Inactive,
      guessNum = 1,
      boards = (0 until numBoards).map(_ =>
        Board(
          currentlyPossibleAnswers = model.validAnswers match {
            case Some(a: BitSet) => a
            case Some(a: Map[_, _]) => BitSet.fromSpecific(a.asInstanceOf[Map[Word, Double]].keys)
            case None => BitSet.fromSpecific(model.resultsCache.wordMapping.indices)
          },
          result = List.empty[ConstraintType],
        )),
    ), Cmd.AdvanceSolver)
  }
}
