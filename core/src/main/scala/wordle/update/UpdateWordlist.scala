package wordle.update

import wordle.Cmd
import wordle.model.*

object UpdateWordlist {
  def apply(model: Model[_], words: IndexedSeq[String]): (Model[_], Cmd) = {
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
