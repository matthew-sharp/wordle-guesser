package wordle.model

import wordle.Cmd
import wordle.util.ResultUtils

trait Solver(scorer: Scorer, pruner: Pruner) {
  def preStats(model: Model): String = {
    if (model.boards.size == 1) s"${model.boards.head.currentlyPossibleAnswers.size} possible words remaining"
    else ""
  }

  def prepGuesses(model: Model): (Model, Cmd)

  def mark(model: Model): (Model, Cmd)

  def prune(model: Model): Model = {
    model.copy(
      state = SolverState.PreStats,
      guessNum = model.guessNum + 1,
    ).pruneBoards(pruner)
  }

  def solved(model: Model): String = ""

  protected def generateBoardScorers(model: Model): Map[Board, (Word, Int) => Double] = {
    val unsolvedBoards = model.boards.filter(b => !b.isSolved)
    model.validAnswers match
      case Some(w: Map[_, _]) => unsolvedBoards.map(b => {
        val validWeights = w.asInstanceOf[Map[Word, Double]].view.filterKeys(b.currentlyPossibleAnswers.contains)
        val preppedScorer = scorer.prepWeightedScoringRound(validWeights.to(Map))
        (b, preppedScorer.weightedScore)
      }).toMap
      case _ => unsolvedBoards.map(b => (b, scorer.score(b.currentlyPossibleAnswers))).toMap
  }
}
