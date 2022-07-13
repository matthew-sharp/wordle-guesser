package wordle.util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class TopNSpec extends AnyFlatSpec with should.Matchers {
  "TopN" should "Return the list if it is N big" in {
    val xs = Map(
      1 -> 4.51
    )
    val expected = List(1)

    val res = TopN(xs, 1)

    res should contain theSameElementsInOrderAs expected
  }

  it should "Return the top 3 out of 5" in {
    val xs = Map(
      1 -> 23.34,
      2 -> 21095.5,
      3 -> 6589230.9658,
      4 -> 610.59,
      5 -> 5968.5
    )

    val expected = List(3, 2, 5)

    val res = TopN(xs, 3)

    res should contain theSameElementsInOrderAs expected
  }

  it should "Just return the list if smaller than n" in {
    val xs = Map(
      1 -> 4.51,
      2 -> 56.1,
    )
    val expected = List(2)

    val res = TopN(xs, 1)

    res should contain theSameElementsInOrderAs expected
  }
}
