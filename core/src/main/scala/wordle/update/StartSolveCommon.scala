package wordle.update

import wordle.Cmd
import wordle.model.*

import scala.collection.immutable.BitSet

object StartSolveCommon {
  def apply[T <: Solver[T]](model: Model[_], solver: T, numBoards: Int): (Model[T], Cmd) = {
    (model.copy(
      solver = solver,
      state = SolverState.Inactive,
      guessNum = 1,
      boards = (0 until numBoards).map(_ =>
        Board(
          currentlyPossibleAnswers = model.validAnswers match {
            case Some(a: BitSet) => a
            case Some(a: IArray[(_, _)]) => BitSet.fromSpecific(a.asInstanceOf[IArray[(Word, Double)]].map(_._1))
            case None => BitSet.fromSpecific(model.resultsCache.wordMapping.indices)
          },
          result = List.empty[ConstraintType],
        )),
    ), Cmd.AdvanceSolver)
  }
}
