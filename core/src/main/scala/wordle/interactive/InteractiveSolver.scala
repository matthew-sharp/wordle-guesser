package wordle.interactive

import wordle.Cmd
import wordle.model.*

import scala.collection.parallel.CollectionConverters.RangeIsParallelizable

case class InteractiveSolver(scorer: Scorer, pruner: Pruner) extends Solver(scorer, pruner) {
  export InteractiveMarker.mark

  def prepGuesses(model: Model): (Model, Cmd) = {
    val boardScorers = generateBoardScorers(model)

    val guessScore = model.resultsCache.wordMapping.indices.par.map(g =>
      (g, model.boards.filter(b => !b.isSolved).map(b =>
        boardScorers(b)(g, model.guessNum)
      ))
    ).seq.toMap

    MenuConsoleBuilder(model, guessScore)
  }
}