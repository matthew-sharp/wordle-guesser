package wordle.model

import scala.collection.immutable.BitSet 

case class Model(
                outputMsg: String,
                resultsCache: CachedResults,
                solver: Solver,
                state: SolverState,
                currentlyPossibleAnswers: BitSet,
                guessNum: Int,
                result: List[Constraint],
                ) {
  def isSolved: Boolean = result.forall(_.constraintType == ConstraintType.Position)
}
