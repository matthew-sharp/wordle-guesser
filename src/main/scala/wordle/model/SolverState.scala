package wordle.model

sealed trait SolverState

object SolverState {
  case object Inactive extends SolverState

  case object PreStats extends SolverState

  case object NeedsMarking extends SolverState

  case object Marked extends SolverState

  case object Solved extends SolverState
}
