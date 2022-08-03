package wordle.model

import wordle.Cmd

import scala.collection.immutable.{BitSet, Queue}

case class Model(
                  batchMode: Boolean, 
                  consoles: List[Console],
                  queuedCmds: Queue[Cmd],
                  queuedSolves: List[String],
                  resultsCache: CachedResults,
                  validAnswers: Option[BitSet | IArray[(Word, Double)]],
                  solver: Solver,
                  state: SolverState,
                  currentGuess: Word,
                  guessNum: Int,
                  boards: Seq[Board],
                ) {
}
