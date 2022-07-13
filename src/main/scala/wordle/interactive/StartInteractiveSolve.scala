package wordle.interactive

import wordle.Cmd
import wordle.entropy.EntropyScorer
import wordle.model.Model
import wordle.update.StartSolveCommon
import wordle.util.LookupPruner

import scala.collection.immutable.BitSet

object StartInteractiveSolve {
  def apply(model: Model, numBoards: Int): (Model, Cmd) = {
    val scorer = EntropyScorer(model.resultsCache)
    val pruner = LookupPruner(model.resultsCache)
    val solver = model.validAnswers match
      case None | Some(_: BitSet) => FlatInteractiveSolver(scorer = scorer, pruner = pruner)
      case Some(_: Map[_, _]) => WeightedInteractiveSolver(scorer = scorer, pruner = pruner)
    StartSolveCommon(model, solver, numBoards)
  }
}
