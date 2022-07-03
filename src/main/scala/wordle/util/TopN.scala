package wordle.util

object TopN {
  def apply[T](sortValue: Map[T, Double], n: Int): IndexedSeq[T] = {
    val elems = sortValue.keys.toList

    implicit val ord: Ordering[T] = Ordering.by(sortValue)
    val q = collection.mutable.PriorityQueue[T](elems:_*)
    val numToReturn = Math.min(n, q.size)
    (1 to numToReturn).map(_ => q.dequeue())
  }
}
