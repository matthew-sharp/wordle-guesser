package wordle.model

import wordle.Cmd

import scala.collection.immutable.{BitSet, Queue} 

case class Model(
                consoles: List[Console],
                queuedCmds: Queue[Cmd],
                resultsCache: CachedResults,
                validAnswers: Option[BitSet],
                solver: Solver,
                state: SolverState,
                currentGuess: Word,
                currentlyPossibleAnswers: BitSet,
                guessNum: Int,
                result: List[Constraint],
                ) {
  def isSolved: Boolean = result.forall(_.constraintType == ConstraintType.Position)
}
