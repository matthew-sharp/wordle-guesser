package wordle.model

import wordle.Cmd

import scala.collection.immutable.{BitSet, Queue}

case class Model[TSolver <: Solver[TSolver]](
                  batchMode: Boolean, 
                  consoles: List[Console],
                  queuedCmds: Queue[Cmd],
                  queuedSolves: List[String],
                  resultsCache: CachedResults,
                  validAnswers: Option[BitSet | IArray[(Word, Double)]],
                  solver: TSolver,
                  state: SolverState,
                  currentGuess: Word,
                  guessNum: Int,
                  boards: Seq[Board],
                ) {
}
