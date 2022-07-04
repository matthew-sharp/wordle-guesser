package wordle.update

import wordle.WordleGuesser
import wordle.auto.AutoSolver
import wordle.entropy.EntropyScorer
import wordle.model._
import wordle.util.{Marker, WordPruner}

object StartAutoSolve {
  def apply(model: Model, word: String): (Model, Cmd) = {
    /*
    val driver = new WordleGuesser(
      words = model.wordlist,
      scorer = new EntropyScorer(model.resultMap),
      pruner = WordPruner,
      guessCallback = identity,
      resultCallback = guess => Marker.mark(guess, word),
    )
    driver
     */
    val solver = AutoSolver(
      answer = word,
      scorer = new EntropyScorer(model.resultMap),
    )
    val newModel = model.copy(
      outputMsg = solver.preStats(model),
      solver = solver,
      state = SolverState.PreStats,
      currentlyPossibleAnswers = model.wordlist,
      guessNum = 1,
    )
    (newModel, Cmd.AdvanceSolver)
  }
}
