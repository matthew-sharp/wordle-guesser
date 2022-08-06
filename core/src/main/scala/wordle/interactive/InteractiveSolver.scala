package wordle.interactive

import wordle.Cmd
import wordle.model.{Board, Word, *}

import scala.collection.immutable.Map
import scala.collection.parallel.CollectionConverters.RangeIsParallelizable

case class InteractiveSolver(scorer: Scorer,
                             pruner: Pruner,
                             boardScorers: Map[Board, (Word, Int) => ScoreInfo],
                            )
  extends Solver(scorer, pruner) {
  export InteractiveMarker.mark

  def prepGuesses[T <: InteractiveSolver](model: Model[InteractiveSolver]): (Model[_], Cmd) = {
    val boardScorers = generateBoardScorers(model)

    val unsolvedBoards = model.boards.filter(b => !b.isSolved)
    
    val guessScore = model.resultsCache.wordMapping.indices.par.map(g =>
      (g, unsolvedBoards.map(b =>
        boardScorers(b)(g, model.guessNum)
      ))
    ).seq.toMap

    MenuConsoleBuilder(model.copy(
      solver = model.solver.copy(
        boardScorers = boardScorers
      )
    ), guessScore)
  }

  def score(w: Word, guessNum: Int): ScoreInfo = {
    val unsolvedBoards = boardScorers.keys.filter(b => !b.isSolved)
    unsolvedBoards.map(b => boardScorers(b)(w, guessNum)).head
  }
}

object InteractiveSolver {
  def apply(scorer: Scorer, pruner: Pruner): InteractiveSolver = InteractiveSolver(scorer = scorer, pruner = pruner, Map.empty)
}