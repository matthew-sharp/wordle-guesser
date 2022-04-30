package wordle

class WordScorer(freqTable: Array[Array[Int]]) {
  def score(word: String): Int = {
    word.toArray.zipWithIndex.map {
      case (c, i) => freqTable(i)(c.toInt - 'a')
    }.sum
  }
}
