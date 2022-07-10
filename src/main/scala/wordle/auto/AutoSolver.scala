package wordle.auto

import wordle.Cmd
import wordle.model.*
import wordle.util.{LookupMarker, Marker, ResultUtils, WordPruner}

import scala.collection.parallel.CollectionConverters.*

case class AutoSolver(
                       answer: Word,
                       scorer: Scorer,
                       pruner: Pruner,
                     ) extends Solver(pruner) {
  override def prepGuesses(model: Model): (Model, Cmd) = {
    val candidateWord = model.resultsCache.wordMapping.indices.par.maxBy { candidate =>
      scorer.score(candidate.toShort, model.currentlyPossibleAnswers, model.guessNum)
    }
    val candidateString = model.resultsCache.wordMapping(candidateWord)
    val annotation = if model.currentlyPossibleAnswers.contains(candidateWord) then "" else "*"
    (model.setOutputMsg(s"selecting guess: \"$candidateString\"$annotation")
      .copy(currentGuess = candidateWord), Cmd.AdvanceSolver)
  }

  override def mark(model: Model): (Model, Cmd) = {
    val cons = LookupMarker.mark(model.resultsCache)(model.currentGuess, answer)
    (model.setOutputMsg(s"result:           ${ResultUtils.toResultString(cons)}")
      .copy(result = cons), Cmd.AdvanceSolver)
  }


  override def solved(model: Model): String = {
    s"answer \"${answerString(model)}\" found in ${model.guessNum} guesses"
  }

  inline def answerString(model: Model): String = model.resultsCache.wordMapping(answer)
}
