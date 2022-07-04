package wordle.update

import wordle.model.{Cmd, Model, SolverState}

object AdvanceSolverUpd {
  def apply(model: Model): (Model, Cmd) = {
    model.state match {
      case SolverState.PreStats => (model.solver.prepGuesses(model), Cmd.AdvanceSolver)
      case SolverState.SelectingGuess => (model.solver.mark(model), Cmd.AdvanceSolver)
    }
  }
}
