package wordle.interactive

import wordle.Cmd
import wordle.model.*
import wordle.util.TopN

import scala.collection.mutable
import scala.collection.parallel.CollectionConverters.RangeIsParallelizable

case class InteractiveSolver(scorer: Scorer, pruner: Pruner) extends Solver (pruner) {
  def prepGuesses(model: Model): (Model, Cmd) = {
    def menuPrompt(menu: Seq[((Word, Double, String), Int)]): String = {
      val sb = mutable.StringBuilder(s"${menu.size} best words:\n")
      menu.foreach(i =>
        sb ++= s"${i._2}.\t${model.resultsCache.wordMapping(i._1._1)}${i._1._3}\t${i._1._2}\n"
      )
      sb ++= "Enter number from menu or type guess word"
      sb.toString
    }

    val guessesByScore = model.resultsCache.wordMapping.indices.par.map(
      g => (g, scorer.score(g, model.currentlyPossibleAnswers, model.guessNum))
    ).seq.toMap
    val topWords = TopN(guessesByScore, 10).takeWhile(guessesByScore(_) > 0)
    val menu = topWords.map(w => (
      w, guessesByScore(w), if (model.currentlyPossibleAnswers.contains(w)) "" else "*")).zipWithIndex
    val inputMapping = menu.map(i => i._2 -> i._1._1).toMap
    val console = Console(
      outputMsg = menuPrompt(menu),
      prompt = ">int>select-guess>",
      parseCallback = InteractiveMenuParser.parse(inputMapping, model.resultsCache.reverseWordMapping),
    )
    (model.pushConsole(console), Cmd.Nothing)
  }

  def mark(model: Model): (Model, Cmd) =
    val console = Console(
      outputMsg = s"Result for ${model.resultsCache.wordMapping(model.currentGuess)}?",
      prompt = ">int>ask-result>",
      parseCallback = ResultParser.parse
    )
    (model.pushConsole(console), Cmd.Nothing)
}
