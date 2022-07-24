package wordle.auto

import wordle.Cmd
import wordle.model.*
import wordle.util.{LookupMarker, Marker, ResultUtils, WordPruner}

import scala.collection.parallel.CollectionConverters.*

case class AutoSolver(
                       answer: Word,
                       scorer: Scorer,
                       pruner: Pruner,
                       quietMode: Boolean,
                     ) extends Solver(scorer, pruner) {
  override def prepGuesses(model: Model): (Model, Cmd) = {
    val boardScorers = generateBoardScorers(model)
    
    val candidateWord = model.resultsCache.wordMapping.indices.par.minBy { candidate =>
      model.boards.map(b =>
        boardScorers(b)(candidate, model.guessNum).score
      ).sum
    }
    val candidateString = model.resultsCache.wordMapping(candidateWord)
    val prob = if model.boards.size == 1 then {
      val candidateProb = boardScorers.head._2(candidateWord, model.guessNum).probability
      f"${candidateProb*100}%.2f%%"
    }
    else "0.00%"
    (model.setOutputMsgIfNotBatch(s"selecting guess: \"$candidateString\" ($prob)")
      .copy(currentGuess = candidateWord), Cmd.AdvanceSolver)
  }

  // Currently this only supports a single board and will drop all other boards
  override def mark(model: Model): (Model, Cmd) = {
    val cons = LookupMarker.mark(model.resultsCache)(model.currentGuess, answer)
    (model.setOutputMsgIfNotBatch(s"result:           ${ResultUtils.toResultString(cons)}")
      .copy(boards = Seq(model.boards.head.copy(result = cons))), Cmd.AdvanceSolver)
  }


  override def solved(model: Model): String = {
    if quietMode
      then s"${answerString(model)},${model.guessNum}"
      else s"answer \"${answerString(model)}\" found in ${model.guessNum} guesses"
  }

  inline def answerString(model: Model): String = model.resultsCache.wordMapping(answer)
}
