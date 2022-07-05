package wordle.update

import wordle.model.{Cmd, Model, SolverState}

object AdvanceSolverUpd {
  def apply(model: Model): (Model, Cmd) = {
    val solver = model.solver
    model.state match {
      case SolverState.Inactive => (model.copy(
        outputMsg = solver.preStats(model),
        state = SolverState.PreStats), Cmd.AdvanceSolver)
      case SolverState.PreStats =>
        (solver.prepGuesses(model).copy(state = SolverState.SelectingGuess), Cmd.AdvanceSolver)
      case SolverState.SelectingGuess => (model.copy(state = SolverState.NeedsMarking), solver.guessCmd)
      case SolverState.NeedsMarking => (solver.mark(model), solver.markCmd)
      case SolverState.Marked => if (model.isSolved)
        (model.copy(state = SolverState.Solved), Cmd.AdvanceSolver)
      else
        {
          val prunedModel = solver.prune(model)
          (prunedModel.copy(outputMsg = solver.preStats(prunedModel)), Cmd.AdvanceSolver)
        }
      case SolverState.Solved => (model.copy(
        outputMsg = solver.solved(model),
        state = SolverState.Inactive),
        Cmd.Prompt)
    }
  }
}
