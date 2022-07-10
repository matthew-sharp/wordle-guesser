package wordle.model

import wordle.Cmd

import scala.collection.immutable.{BitSet, Queue} 

case class Model(
                  consoles: List[Console],
                  queuedCmds: Queue[Cmd],
                  resultsCache: CachedResults,
                  validAnswers: Option[BitSet],
                  solver: Solver,
                  state: SolverState,
                  currentGuess: Word,
                  guessNum: Int,
                  boards: Seq[Board],
                ) {
}
