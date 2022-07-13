package wordle.auto

import wordle.Cmd
import wordle.model.*
import wordle.util.{LookupMarker, Marker, ResultUtils, WordPruner}

import scala.collection.parallel.CollectionConverters.*

case class AutoSolver(
                       answer: Word,
                       scorer: Scorer,
                       pruner: Pruner,
                     ) extends Solver(scorer, pruner) {
  override def prepGuesses(model: Model): (Model, Cmd) = {
    val candidateWord = model.resultsCache.wordMapping.indices.par.maxBy { candidate =>
      model.boards.map(b =>
        scorer.score(b.currentlyPossibleAnswers)(candidate.toShort, model.guessNum)
      ).sum
    }
    val candidateString = model.resultsCache.wordMapping(candidateWord)
    val annotation = if model.boards.size == 1 &&
      !model.boards.head.currentlyPossibleAnswers.contains(candidateWord) then "*" else ""
    (model.setOutputMsg(s"selecting guess: \"$candidateString\"$annotation")
      .copy(currentGuess = candidateWord), Cmd.AdvanceSolver)
  }

  // Currently this only supports a single board and will drop all other boards
  override def mark(model: Model): (Model, Cmd) = {
    val cons = LookupMarker.mark(model.resultsCache)(model.currentGuess, answer)
    (model.setOutputMsg(s"result:           ${ResultUtils.toResultString(cons)}")
      .copy(boards = Seq(model.boards.head.copy(result = cons))), Cmd.AdvanceSolver)
  }


  override def solved(model: Model): String = {
    s"answer \"${answerString(model)}\" found in ${model.guessNum} guesses"
  }

  inline def answerString(model: Model): String = model.resultsCache.wordMapping(answer)
}
