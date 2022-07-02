package wordle

import model.Scorer
import util.{Marker, WordPruner}

import scala.io.StdIn.readLine

class LoopingGuesser() {
  def loop(words: Set[String], scorer: Scorer): Unit = {
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
      )
      val (num, word) = wordGuesser.guess(false)
      println(s"answer \"$word\" found in $num guesses")
    }
  }

}
