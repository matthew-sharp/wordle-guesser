package wordle.update

import wordle.Cmd
import wordle.model.{Model, setOutputMsg}

import scala.collection.immutable.BitSet

object UpdateAnswerList {
  def apply(model: Model, lines: Seq[String]): (Model, Cmd) = {
    val fun = if (lines.take(5).map(_.trim.length).forall(_ == 5))
      setUnweightedAnswers
    else
      setWeightedAnswers

    val updatedModel = fun(model, lines)
    (updatedModel, Cmd.Nothing)
  }

  private def setUnweightedAnswers(model: Model, lines: Seq[String]): Model = {
    model.setOutputMsg(s"${lines.size} answers read")
      .copy(validAnswers = Some(BitSet.fromSpecific(lines.map(model.resultsCache.reverseWordMapping))))
  }

  private def setWeightedAnswers(model: Model, lines: Seq[String]): Model = {
    val weightMap = lines.map(_.split("[\t ]+"))
      .map(w => (model.resultsCache.reverseWordMapping(w(0)), w(1).toDouble))
      .toMap
    model.setOutputMsg(s"${lines.size} answers with weights read")
      .copy(validAnswers = Some(weightMap))
  }
}
