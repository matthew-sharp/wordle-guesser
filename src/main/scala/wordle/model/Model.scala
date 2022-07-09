package wordle.model

import wordle.Cmd

import scala.collection.immutable.{BitSet, Queue} 

case class Model(
                console: Console,
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
  
  def setOutputMsg(outMsg: String): Model = {
    this.copy(console = this.console.copy(outputMsg = outMsg))
  }
}
