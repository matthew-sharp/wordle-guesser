package wordle.interactive

import wordle.Cmd
import wordle.model.*

import scala.collection.parallel.CollectionConverters.RangeIsParallelizable

class FlatInteractiveSolver(scorer: Scorer, pruner: Pruner) extends Solver(pruner) {
  export InteractiveMarker.mark

  def prepGuesses(model: Model): (Model, Cmd) = {
    val guessesByScore = model.resultsCache.wordMapping.indices.par.map(g =>
      (g, model.boards.filter(b => !b.isSolved).map(b =>
        scorer.score(g, b.currentlyPossibleAnswers, model.guessNum)).sum
      )
    ).seq.toMap

    MenuConsoleBuilder(model, guessesByScore)
  }
}
