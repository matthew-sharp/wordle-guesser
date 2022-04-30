package wordle

import java.io.{FileOutputStream, ObjectOutputStream}
import scala.io.Source

object freqGenerator extends App {
  val words = Source.fromFile("wordlist").getLines().toList
  val freqMatrix = words.map { w =>
    val vector = Array.ofDim[Int](5, 26)
    for (i <- 0 to 4) {
      vector(i)(w(i) - 'a') = 1
    }
    vector
  }.reduce(matrixSum)

  val freqWriter = new ObjectOutputStream(new FileOutputStream("freq-table"))
  freqWriter.writeObject(freqMatrix)
  freqWriter.close()

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
