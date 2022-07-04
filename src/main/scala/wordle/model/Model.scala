package wordle.model

case class Model(
                outputMsg: String,
                wordlist: Set[String],
                resultMap: Map[String, Map[String, Short]],
                solver: Solver,
                state: SolverState,
                currentlyPossibleAnswers: Set[String],
                guessNum: Int,
                result: Seq[Constraint],
                ) {
  def isSolved: Boolean = result.forall(_.constraintType == ConstraintType.Position)
}
