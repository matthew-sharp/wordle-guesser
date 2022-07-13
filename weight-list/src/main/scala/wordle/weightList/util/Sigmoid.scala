package wordle.weightList.util

object Sigmoid {
  def apply(halfPoint: Int, gradient: Double)(xs: Seq[String]): Seq[(String, Double)] = {
    xs.zipWithIndex.map((s, idx) => (s, 1 / (1 + Math.exp(-gradient * (idx - halfPoint)))))
  }
}
