package wordle.update

import wordle.model.{Cmd, Model, SolverState}

object AdvanceSolverUpd {
  def apply(model: Model): (Model, Cmd) = {
    val solver = model.solver
    model.state match {
      case SolverState.Inactive => (model.copy(state = SolverState.PreStats), Cmd.AdvanceSolver)
      case SolverState.PreStats => (solver.prepGuesses(model), Cmd.AdvanceSolver)
      case SolverState.SelectingGuess => (model.copy(state = SolverState.NeedsMarking), solver.guessCmd)
      case SolverState.NeedsMarking => (solver.mark(model), solver.markCmd)
      case SolverState.Marked => if (model.isSolved)
        (model.copy(state = SolverState.Solved), Cmd.AdvanceSolver)
      else
        (solver.prune(model), Cmd.AdvanceSolver)
      case SolverState.Solved => (model.copy(
        outputMsg = solver.solved(model),
        state = SolverState.Inactive),
        Cmd.Prompt)
    }
  }
}
