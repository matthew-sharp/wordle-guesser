package wordle.util

object FrequencyCalculator {
  def calc(words: Set[String]): Array[Array[Int]] = words.map { w =>
    val vector = Array.ofDim[Int](5, 26)
    for (i <- 0 to 4) {
      vector(i)(w(i) - 'a') = 1
    }
    vector
  }.reduce(matrixSum)

  def calcLetterFreq(freqTable: Array[Array[Int]]): Array[Int] = {
    val letterFreq = new Array[Int](26)
    for (j <- 0 to 25) {
      letterFreq(j) = (0 to 4).foldLeft(0)((sum, i) => freqTable(i)(j) + sum)
    }
    letterFreq
  }

  def matrixSum(m1: Array[Array[Int]], m2: Array[Array[Int]]): Array[Array[Int]] = {
    val rows = 5
    val cols = 26
    val ret = Array.ofDim[Int](rows, cols)
    for (r <- 0 until rows) {
      for (c <- 0 until cols) {
        ret(r)(c) = m1(r)(c) + m2(r)(c)
      }
    }
    ret
  }
}
