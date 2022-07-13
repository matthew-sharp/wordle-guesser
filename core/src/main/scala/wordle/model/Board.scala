package wordle.model

import wordle.util.ResultUtils

import scala.collection.immutable.BitSet

case class Board(
                  currentlyPossibleAnswers: BitSet,
                  result: List[ConstraintType],
                ) {
  def isSolved: Boolean = result.nonEmpty && result.forall(_ == ConstraintType.Position)
}

extension (m: Model) {
  def setBoardResult(boardNum: Int)(cons: List[ConstraintType]): Model = {
    val currentBoards = m.boards
    val board = currentBoards(boardNum)
    val newBoard = board.copy(result = cons)
    m.copy(boards = currentBoards.updated(boardNum, newBoard))
  }

  def pruneBoards(pruner: Pruner) = {
    val newBoards = m.boards.map(
      b => b.copy(
        currentlyPossibleAnswers = pruner.pruneWords(
          b.currentlyPossibleAnswers,
          ResultUtils.toTernary(b.result),
          m.currentGuess)
      )
    )
    m.copy(boards = newBoards)
  }
}