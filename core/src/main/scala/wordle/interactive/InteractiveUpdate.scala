package wordle.interactive

import wordle.interactive.MsgInteractive.*
import wordle.model.*
import wordle.{Cmd, Msg}

object InteractiveUpdate {
  def update(msg: Msg, model: Model[InteractiveSolver]): Option[(Model[InteractiveSolver], Cmd)] = {
    msg match
      case SetGuess(g) => Some((model.copy(currentGuess = g).popConsole, Cmd.AdvanceSolver))
      case SetResult(r, n) =>
        val poppedModel = model.setBoardResult(n)(r).popConsole
        Some((poppedModel,
          if (poppedModel.consoles.exists(c => c.conType == ResultConsole)) Cmd.Nothing
          else Cmd.AdvanceSolver))
      case InteractiveSolve(n) => Some(StartInteractiveSolve(model, n))
      case ScoreSingleWord(w) => Some(model.setOutputMsgIfNotBatch(
        model.solver.score(w, model.guessNum).toString)
        , Cmd.Nothing)
      case Abort => Some(model.popConsole, Cmd.Nothing)
      case _ => None
  }
}
