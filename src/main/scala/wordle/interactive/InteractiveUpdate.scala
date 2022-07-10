package wordle.interactive

import wordle.interactive.MsgInteractive.*
import wordle.model.{Model, popConsole, setBoardResult}
import wordle.{Cmd, Msg}

object InteractiveUpdate {
  def update(msg: Msg, model: Model): Option[(Model, Cmd)] = {
    msg match
      case SetGuess(g) => Some((model.copy(currentGuess = g).popConsole, Cmd.AdvanceSolver))
      case SetResult(r, n) => Some((model.setBoardResult(n)(r).popConsole, Cmd.AdvanceSolver))
      case InteractiveSolve => Some(StartInteractiveSolve(model))
      case Abort => Some(model.popConsole, Cmd.Nothing)
      case _ => None
  }
}
