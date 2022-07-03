package wordle.update

import wordle.model.{Cmd, Model}

object UpdateWordlist {
  def apply(model: Model, words: Set[String]): (Model, Cmd) = {
    (model.copy(
      outputMsg = s"${words.size} words read",
      wordlist = words),
      Cmd.SetResultMap)
  }
}
