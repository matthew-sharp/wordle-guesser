package wordle.update

import wordle.Cmd
import wordle.model.*

object AdvanceSolverUpd {
  def apply(model: Model): (Model, Cmd) = {
    val solver = model.solver
    model.state match {
      case SolverState.Inactive => (model.setOutputMsgIfNotBatch(solver.preStats(model))
        .copy(state = SolverState.PreStats),
        Cmd.AdvanceSolver)
      case SolverState.PreStats =>
        val mdl = model.copy(state = SolverState.NeedsMarking)
        solver.prepGuesses(mdl)
      case SolverState.NeedsMarking =>
        val mdl = model.copy(state = SolverState.Marked)
        solver.mark(mdl)
      case SolverState.Marked =>
        if (model.boards.forall(b => b.isSolved))
          (model
            .copy(state = SolverState.Inactive)
            .setOutputMsgEvenIfBatch(solver.solved(model)),
            Cmd.Nothing)
        else {
          val prunedModel = solver.prune(model)
          (prunedModel.setOutputMsgIfNotBatch(solver.preStats(prunedModel)), Cmd.AdvanceSolver)
        }
    }
  }
}
