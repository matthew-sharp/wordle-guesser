package wordle

import scala.io.Source

object WordleAutoApp extends App {
  val answer = args(0)

  val rows = 5
  val cols = 26

  val wordsSource = 
    if (Files.exists(Paths.get("wordlist"))) {
      Source.fromFile("wordlist")
    }
    else {
      println("wordlist not found in current directory, using built-in word list")
      Source.fromResource("wordlist")
    }
  var words = wordsSource.getLines().toSet
  wordsSource.close()
  var freqTable = Array.ofDim[Int](rows, cols)

  while (true) {
    println(s"${words.size} possible words")
    print("selecting candidate: ")
    freqTable = FrequencyCalculator.calc(words)
    val candidateWord = words.maxBy(WordScorer.score(_, freqTable))
    println(candidateWord)
    val cons = Marker.mark(candidateWord, answer)
    if (cons.forall(_.constraintType == ConstraintType.Position)) System.exit(0)
    words = WordPruner.pruneWords(words, cons)
  }
}
