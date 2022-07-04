package wordle.update

import wordle.model.{Cmd, Model, SolverState}

object AdvanceSolverUpd {
  def apply(model: Model): (Model, Cmd) = {
    val solver = model.solver
    model.state match {
      case SolverState.PreStats => (solver.prepGuesses(model), Cmd.AdvanceSolver)
      case SolverState.SelectingGuess => (model, solver.guessCmd)
      case SolverState.NeedsMarking => (solver.mark(model), solver.markCmd)
      case SolverState.Marked => if (model.isSolved)
        (solver.prune(model), Cmd.AdvanceSolver)
      else
        (model.copy(state = SolverState.Solved), Cmd.AdvanceSolver)
      case SolverState.Solved => (model.copy(state = SolverState.Inactive), Cmd.Prompt)
    }
  }
}
