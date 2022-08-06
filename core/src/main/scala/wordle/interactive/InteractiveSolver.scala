package wordle.interactive

import wordle.Cmd
import wordle.model.*

import scala.collection.parallel.CollectionConverters.RangeIsParallelizable

case class InteractiveSolver(scorer: Scorer, pruner: Pruner) extends Solver(scorer, pruner) {
  export InteractiveMarker.mark

  def prepGuesses(model: Model[_]): (Model[_], Cmd) = {
    val boardScorers = generateBoardScorers(model)

    val unsolvedBoards = model.boards.filter(b => !b.isSolved)
    
    val guessScore = model.resultsCache.wordMapping.indices.par.map(g =>
      (g, unsolvedBoards.map(b =>
        boardScorers(b)(g, model.guessNum)
      ))
    ).seq.toMap

    MenuConsoleBuilder(model, guessScore)
  }
}