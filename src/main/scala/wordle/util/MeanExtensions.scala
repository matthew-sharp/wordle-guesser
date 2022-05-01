package wordle.util

import scala.language.implicitConversions

object MeanExtensions {
  implicit class MeanExtensions(val elems: Seq[Int]) {
    def geoMean: Double = {
      val num = elems.size
      val sumLog = elems.map(Math.log(_)).sum
      Math.exp(sumLog/num)
    }

    def logSumExp: Double = {
      elems.map(Math.log(_)).sum
    }
  }
}
