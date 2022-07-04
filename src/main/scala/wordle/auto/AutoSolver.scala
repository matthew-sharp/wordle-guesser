package wordle.auto

import wordle.model.{Model, Scorer, Solver, SolverState}
import wordle.util.{Marker, ResultUtils}

import scala.collection.parallel.CollectionConverters._

case class AutoSolver(
                     answer: String,
                       scorer: Scorer,
                 candidateWord: String,
                ) extends Solver {
  def preStats(model: Model): String = {
    s"${model.currentlyPossibleAnswers.size} possible words remaining"
  }

  def prepGuesses(model: Model): Model = {
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
      state = SolverState.Marked,
    )
  }
}

object AutoSolver {
  def apply(answer: String,
            scorer: Scorer): AutoSolver = new AutoSolver(answer, scorer, "")
}