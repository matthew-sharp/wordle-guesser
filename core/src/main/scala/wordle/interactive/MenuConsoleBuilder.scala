package wordle.interactive

import wordle.Cmd
import wordle.model.*
import wordle.util.TopN

import scala.collection.mutable

object MenuConsoleBuilder {
  def apply(model: Model, guessScoreInfo: Map[Word, Seq[ScoreInfo]]): (Model, Cmd) = {
    def menuPrompt(menu: Seq[((Word, Seq[ScoreInfo], String), Int)]): String = {
      val sb = mutable.StringBuilder(s"${menu.size} best words:\n")
      sb ++= "\t\t\test guesses\tP(word)\tE(word)\n"
      menu.foreach(i =>
        val scoreInfo = i._1._2
        val totalScore = scoreInfo.map(_.score).sum
        val infoSuffix = if scoreInfo.size >1 then
          f"$totalScore%.6f"
        else {
          val si = scoreInfo.head
          f"$totalScore%.6f\t${si.probability*100}%.2f%%\t${si.rawScore}%.6f"
        }
        sb ++= s"${i._2}.\t${model.resultsCache.wordMapping(i._1._1)}${i._1._3}\t$infoSuffix\n"
      )
      sb ++= "Enter number from menu or type guess word"
      sb.toString
    }

    val guessScore = guessScoreInfo.view.mapValues(_.map(_.score).sum).toMap
    val topWords = TopN(guessScore, 10).takeWhile(guessScore(_) > 0)
    val menu = topWords.map(w => (
      w, guessScoreInfo(w),
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
