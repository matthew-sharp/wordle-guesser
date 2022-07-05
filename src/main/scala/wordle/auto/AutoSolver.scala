package wordle.auto

import wordle.model.*
import wordle.util.{LookupMarker, Marker, ResultUtils, WordPruner}

import scala.collection.parallel.CollectionConverters.*

case class AutoSolver(
                     answer: Word,
                       scorer: Scorer,
                     pruner: Pruner,
                 candidateWord: Word,
                ) extends Solver(Cmd.AdvanceSolver, Cmd.AdvanceSolver) {

  override def preStats(model: Model): String = {
    s"${model.currentlyPossibleAnswers.size} possible words remaining"
  }

  override def prepGuesses(model: Model): Model = {
    val candidateWord = model.resultsCache.wordMapping.indices.par.maxBy { candidate =>
      scorer.score(candidate.toShort, model.currentlyPossibleAnswers, model.guessNum)
    }
    val newSolver = this.copy(candidateWord = candidateWord)
    val candidateString = model.resultsCache.wordMapping(candidateWord)
    val annotation = if model.currentlyPossibleAnswers.contains(candidateWord) then "" else "*"
    model.copy(
      outputMsg = s"selecting guess: \"$candidateString\"$annotation",
      state = SolverState.SelectingGuess,
      solver = newSolver)
  }

  override def mark(model: Model): Model = {
    val cons = LookupMarker.mark(model.resultsCache)(candidateWord, answer)
    model.copy(
      outputMsg = s"result:           ${ResultUtils.toResultString(cons.map(_.constraintType))}",
      result = cons,
      state = SolverState.Marked,
    )
  }

  override def prune(model: Model): Model = {
    model.copy(
      state = SolverState.PreStats,
      guessNum = model.guessNum + 1,
      currentlyPossibleAnswers = pruner.pruneWords(
      model.currentlyPossibleAnswers,
        ResultUtils.toTernary(model.result),
      candidateWord)
    )
  }

  override def solved(model: Model): String = {
    s"answer \"${answerString(model)}\" found in ${model.guessNum} guesses"
  }

  inline def answerString(model: Model): String = model.resultsCache.wordMapping(answer)
}

object AutoSolver {
  def apply(answer: Word,
            scorer: Scorer,
            pruner: Pruner): AutoSolver =
    new AutoSolver(answer, scorer, pruner, -1)
}