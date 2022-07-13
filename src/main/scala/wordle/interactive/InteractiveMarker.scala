package wordle.interactive

import wordle.Cmd
import wordle.model.*

object InteractiveMarker {
  def mark(model: Model): (Model, Cmd) =
    val consoles = model.boards.zipWithIndex.filter((b, _) => !b.isSolved).map((_, idx) =>
      Console(
        outputMsg = s"Result for ${model.resultsCache.wordMapping(model.currentGuess)} on board ${idx+1}?",
        prompt = s">int>ask-result(${idx+1})>",
        parseCallback = ResultParser.parse(idx),
        conType = ResultConsole,
      )
    )
    (model.pushConsoles(consoles), Cmd.Nothing)
}
