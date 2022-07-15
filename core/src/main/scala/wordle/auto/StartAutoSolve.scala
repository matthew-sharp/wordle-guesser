package wordle.auto

import wordle.Cmd
import wordle.entropy.EntropyScorer
import wordle.model.{Model, setOutputMsg}
import wordle.update.StartSolveCommon
import wordle.util.LookupPruner

object StartAutoSolve {
  def apply(model: Model, answer: String): (Model, Cmd) = {
    val ansIdx = model.resultsCache.reverseWordMapping.get(answer)
    ansIdx.map { answer =>
      AutoSolver(
        answer = answer,
        scorer = EntropyScorer(model.resultsCache),
        pruner = LookupPruner(model.resultsCache),
      )
    }.map(solver => StartSolveCommon(model, solver, 1))
      .getOrElse((model.setOutputMsg(s"$answer not in master wordlist"), Cmd.Nothing))
  }
}
