package wordle.util

object MemoizedLog {
  val cache = collection.mutable.Map.empty[Int, Double]

  def apply(i: Int): Double = {
    cache.getOrElseUpdate(i, Math.log(i))
  }
}
