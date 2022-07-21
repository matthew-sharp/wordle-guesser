package wordle.update

import wordle.Cmd
import wordle.model.*

object UpdateWordlist {
  def apply(model: Model, words: IndexedSeq[String]): (Model, Cmd) = {
    (model.copy(
      resultsCache = CachedResults(
        IArray.empty[ResultTernary],
        words,
        Map.empty[String, Word]
      ),
    ).setOutputMsgIfNotBatch(s"${words.size} words read"),
      Cmd.SetResultMap)
  }
}
