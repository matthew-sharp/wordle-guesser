package wordle.update

import wordle.Cmd
import wordle.model._
import wordle.model.{CachedResults, Model, ResultTernary}

object UpdateWordlist {
  def apply(model: Model, words: IndexedSeq[String]): (Model, Cmd) = {
    (model.copy(
      resultsCache = CachedResults(
        Array.empty[ResultTernary],
        words,
        Map.empty[String, Word]
      ),
    ).setOutputMsg(s"${words.size} words read"),
      Cmd.SetResultMap)
  }
}
