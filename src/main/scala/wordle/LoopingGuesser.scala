package wordle

import model.Scorer
import util.{Marker, WordPruner}

import java.nio.file.{Files, Paths}
import scala.io.StdIn.readLine

class LoopingGuesser() {
  def loop(words: Set[String], scorer: Scorer, answerWords: Option[Set[String]] = None): Unit = {
    val perfFile = Files.newOutputStream(Paths.get("results"))
    while(true) {
      print("enter word to solve: ")
      val answer = readLine()
      val wordGuesser = new WordleGuesser(
        words,
        scorer,
        WordPruner.pruneWords,
        candidate => {
          print("selecting candidate: ")
          println(candidate)
          candidate
        },
        resultCallback = guess => Marker.mark(guess, answer),
        answerWords
      )
      val (num, word) = wordGuesser.guess(false)
      println(s"answer \"$word\" found in $num guesses")
      perfFile.write(s"$word,$num\n".getBytes())
    }
  }
}
