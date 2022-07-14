package wordle.update

import wordle.Cmd
import wordle.model.{Model, setOutputMsg}

object UpdateWeightedAnswerList {
  def apply(model: Model, lines: Seq[String]): (Model, Cmd) = {
    val weightMap = lines.map(_.split("[\t ]+"))
      .map(w => (model.resultsCache.reverseWordMapping(w(0)), w(1).toDouble))
      .toMap
    (model.setOutputMsg(s"${lines.size} answers read")
      .copy(validAnswers = Some(weightMap)), Cmd.Nothing)
  }
}
