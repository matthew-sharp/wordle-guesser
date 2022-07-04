package wordle.auto

import wordle.model._
import wordle.util.{Marker, ResultUtils, WordPruner}

import scala.collection.parallel.CollectionConverters._

case class AutoSolver(
                     answer: String,
                       scorer: Scorer,
                     pruner: Pruner,
                 candidateWord: String,
                ) extends Solver {
  override val guessCmd: Cmd = Cmd.AdvanceSolver
  override val markCmd: Cmd = Cmd.AdvanceSolver

  def preStats(model: Model): String = {
    s"${model.currentlyPossibleAnswers.size} possible words remaining"
  }

  override def prepGuesses(model: Model): Model = {
    val candidateWord = model.currentlyPossibleAnswers.par.maxBy { candidate =>
      scorer.score(candidate, model.currentlyPossibleAnswers, model.guessNum)
    }
    val newSolver = this.copy(candidateWord = candidateWord)
    model.copy(
      outputMsg = s"selecting guess: \"$candidateWord\"",
      state = SolverState.SelectingGuess,
      solver = newSolver)
  }

  override def mark(model: Model): Model = {
    val cons = Marker.mark(candidateWord, answer)
    model.copy(
      outputMsg = s"Result: ${ResultUtils.toResultString(cons.map(_.constraintType))}",
      result = cons,
      state = SolverState.Marked,
    )
  }

  override def prune(model: Model): Model = {
    model.copy(currentlyPossibleAnswers = pruner.pruneWords(model.currentlyPossibleAnswers, model.result))
  }
}

object AutoSolver {
  def apply(answer: String,
            scorer: Scorer): AutoSolver =
    new AutoSolver(answer, scorer, WordPruner, "")
}