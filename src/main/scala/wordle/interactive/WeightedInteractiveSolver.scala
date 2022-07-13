package wordle.interactive

import wordle.Cmd
import wordle.model.*

import scala.collection.parallel.CollectionConverters.RangeIsParallelizable

case class WeightedInteractiveSolver(scorer: WeightedScorer, pruner: Pruner) extends Solver(pruner) {
  export InteractiveMarker.mark

  def prepGuesses(model: Model): (Model, Cmd) = {
    val boardScorers: Map[Board, (Word, Int) => Double] = model.validAnswers match
      case Some(w: Map[_, _]) => model.boards.filter(b => !b.isSolved).map(b => {
        val validWeights = w.asInstanceOf[Map[Word, Double]].view.filterKeys(b.currentlyPossibleAnswers.contains)
        val preppedScorer = scorer.prepWeightedScoringRound(validWeights.to(Map))
        (b, preppedScorer.weightedScore)
      }).toMap
      // Something has gone very wrong if we get here
      case _ => Map.empty[Board, (Word, Int) => Double]

    val guessScore = model.resultsCache.wordMapping.indices.par.map(g =>
      (g, model.boards.filter(b => !b.isSolved).map(b =>
        boardScorers(b)(g, model.guessNum)).sum
      )
    ).seq.toMap

    MenuConsoleBuilder(model, guessScore)
  }
}