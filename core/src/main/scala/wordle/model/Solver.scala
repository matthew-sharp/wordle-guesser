package wordle.model

import wordle.Cmd
import wordle.util.ResultUtils

trait Solver[S](scorer: Scorer, pruner: Pruner) {
  this: S =>
  def preStats(model: Model[_]): String = {
    if (model.boards.size == 1) s"${model.boards.head.currentlyPossibleAnswers.size} possible words remaining"
    else ""
  }

  def prepGuesses(model: Model[S]): (Model[_], Cmd)

  def mark(model: Model[_]): (Model[_], Cmd)

  def prune(model: Model[_]): Model[_] = {
    model.copy(
      state = SolverState.PreStats,
      guessNum = model.guessNum + 1,
    ).pruneBoards(pruner)
  }

  def solved(model: Model[_]): String = ""

  protected def generateBoardScorers(model: Model[_]): Map[Board, (Word, Int) => ScoreInfo] = {
    val unsolvedBoards = model.boards.filter(b => !b.isSolved)
    model.validAnswers match
      case Some(w: IArray[(_, _)]) => unsolvedBoards.map(b => {
        val validWeights = w.asInstanceOf[IArray[(Word, Double)]].filter((word, _) => b.currentlyPossibleAnswers.contains(word))
        val preppedScorer = scorer.prepWeightedScoringRound(validWeights)
        (b, preppedScorer.weightedScore)
      }).toMap
      case _ => unsolvedBoards.map(b => (b, scorer.flatScore(IArray.from(b.currentlyPossibleAnswers)))).toMap
  }
}
