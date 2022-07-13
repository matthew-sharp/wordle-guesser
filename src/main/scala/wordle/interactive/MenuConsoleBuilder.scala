package wordle.interactive

import wordle.Cmd
import wordle.model.*
import wordle.util.TopN

import scala.collection.mutable

object MenuConsoleBuilder {
  def apply(model: Model, guessScore: Map[Word, Double]): (Model, Cmd) = {
    def menuPrompt(menu: Seq[((Word, Double, String), Int)]): String = {
      val sb = mutable.StringBuilder(s"${menu.size} best words:\n")
      menu.foreach(i =>
        sb ++= s"${i._2}.\t${model.resultsCache.wordMapping(i._1._1)}${i._1._3}\t${i._1._2}\n"
      )
      sb ++= "Enter number from menu or type guess word"
      sb.toString
    }

    val topWords = TopN(guessScore, 10).takeWhile(guessScore(_) > 0)
    val menu = topWords.map(w => (
      w, guessScore(w),
      if (model.boards.size == 1 && !model.boards.head.currentlyPossibleAnswers.contains(w)) "*" else "")).zipWithIndex
    val inputMapping = menu.map(i => i._2 -> i._1._1).toMap
    val console = Console(
      outputMsg = menuPrompt(menu),
      prompt = ">int>select-guess>",
      parseCallback = InteractiveMenuParser.parse(inputMapping, model.resultsCache.reverseWordMapping),
    )
    (model.pushConsole(console), Cmd.Nothing)

  }
}
