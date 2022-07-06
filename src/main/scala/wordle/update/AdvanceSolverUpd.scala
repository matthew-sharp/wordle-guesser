package wordle.update

import wordle.Cmd
import wordle.model.{Model, SolverState}

object AdvanceSolverUpd {
  def apply(model: Model): (Model, Cmd) = {
    val solver = model.solver
    model.state match {
      case SolverState.Inactive => (model.copy(
        outputMsg = solver.preStats(model),
        state = SolverState.PreStats), Cmd.AdvanceSolver)
      case SolverState.PreStats =>
        val mdl = model.copy(state = SolverState.NeedsMarking)
        solver.prepGuesses(mdl)
      case SolverState.NeedsMarking => 
        val mdl = model.copy(state = SolverState.Marked)
        solver.mark(mdl)
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
