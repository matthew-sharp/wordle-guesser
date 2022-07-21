package wordle.auto

import wordle.Cmd
import wordle.entropy.EntropyScorer
import wordle.model.{Model, setOutputMsgIfNotBatch}
import wordle.update.StartSolveCommon
import wordle.util.LookupPruner

object StartAutoSolve {
  def apply(model: Model, answer: Option[String]): (Model, Cmd) = {
    val (updatedModel, ansString) = answer match
      case Some(ans) => (model, ans)
      case None => (model.copy(queuedSolves = model.queuedSolves.tail), model.queuedSolves.head)
    val ansIdx = model.resultsCache.reverseWordMapping.get(ansString)
    ansIdx.map { answer =>
      AutoSolver(
        answer = answer,
        scorer = EntropyScorer(model.resultsCache),
        pruner = LookupPruner(model.resultsCache),
        quietMode = model.batchMode,
      )
    }.map(solver => StartSolveCommon(updatedModel, solver, 1))
      .getOrElse((updatedModel.setOutputMsgIfNotBatch(s"$answer not in master wordlist"), Cmd.Nothing))
  }
}
