package wordle.util

import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import wordle.model.{Constraint, ConstraintType}

class MarkerSpec extends AnyFlatSpec with should.Matchers {
  "Marker.mark" should "correctly mark all black for complete miss" in {
    val res = Marker.mark("adieu", "bcfgh")

    val expected = List[Constraint](
      Constraint('a', ConstraintType.Absent),
      Constraint('d', ConstraintType.Absent),
      Constraint('i', ConstraintType.Absent),
      Constraint('e', ConstraintType.Absent),
      Constraint('u', ConstraintType.Absent),
    )
    res should contain theSameElementsInOrderAs expected
  }

  it should "correctly mark green on a full match" in {
    val res = Marker.mark("abcde", "abcde")

    val expected = List[Constraint](
      Constraint('a', ConstraintType.Position),
      Constraint('b', ConstraintType.Position),
      Constraint('c', ConstraintType.Position),
      Constraint('d', ConstraintType.Position),
      Constraint('e', ConstraintType.Position),
    )
    res should contain theSameElementsInOrderAs expected
  }

  it should "mark one yellow one black for an incorrect double" in {
    val res = Marker.mark("essay", "etals")

    val expected = List[Constraint](
      Constraint('e', ConstraintType.Position),
      Constraint('s', ConstraintType.Exists),
      Constraint('s', ConstraintType.Absent),
      Constraint('a', ConstraintType.Exists),
      Constraint('y', ConstraintType.Absent),
    )
    res should contain theSameElementsInOrderAs expected
  }

  it should "not give a yellow when a later green would come" in {
    val res = Marker.mark("local", "focal")

    val expected = List[Constraint](
      Constraint('l', ConstraintType.Absent),
      Constraint('o', ConstraintType.Position),
      Constraint('c', ConstraintType.Position),
      Constraint('a', ConstraintType.Position),
      Constraint('l', ConstraintType.Position),
    )
    res should contain theSameElementsInOrderAs expected
  }

  it should "give a yellow when there's a later green but the yellow still should be there" in {
    val res = Marker.mark("aabbb", "cacac")

    val expected = List[Constraint](
      Constraint('a', ConstraintType.Exists),
      Constraint('a', ConstraintType.Position),
      Constraint('b', ConstraintType.Absent),
      Constraint('b', ConstraintType.Absent),
      Constraint('b', ConstraintType.Absent),
    )
    res should contain theSameElementsInOrderAs expected
  }

  it should "give a correct yellow when following a green" in {
    val res = Marker.mark("cooly", "colon")

    val expected = List[Constraint](
      Constraint('c', ConstraintType.Position),
      Constraint('o', ConstraintType.Position),
      Constraint('o', ConstraintType.Exists),
      Constraint('l', ConstraintType.Exists),
      Constraint('y', ConstraintType.Absent),
    )
    res should contain theSameElementsInOrderAs expected
  }

  it should "mark speed correctly when answer is abide" in {
    val res = Marker.mark("speed", "abide")

    val expected = List[Constraint](
      Constraint('s', ConstraintType.Absent),
      Constraint('p', ConstraintType.Absent),
      Constraint('e', ConstraintType.Exists),
      Constraint('e', ConstraintType.Absent),
      Constraint('d', ConstraintType.Exists),
    )
    res should contain theSameElementsInOrderAs expected
  }

  it should "mark speed correctly when answer is erase" in {
    val res = Marker.mark("speed", "erase")

    val expected = List[Constraint](
      Constraint('s', ConstraintType.Exists),
      Constraint('p', ConstraintType.Absent),
      Constraint('e', ConstraintType.Exists),
      Constraint('e', ConstraintType.Exists),
      Constraint('d', ConstraintType.Absent),
    )
    res should contain theSameElementsInOrderAs expected
  }

  it should "mark speed correctly when answer is steal" in {
    val res = Marker.mark("speed", "steal")

    val expected = List[Constraint](
      Constraint('s', ConstraintType.Position),
      Constraint('p', ConstraintType.Absent),
      Constraint('e', ConstraintType.Position),
      Constraint('e', ConstraintType.Absent),
      Constraint('d', ConstraintType.Absent),
    )
    res should contain theSameElementsInOrderAs expected
  }

  it should "mark speed correctly when answer is crepe" in {
    val res = Marker.mark("speed", "crepe")

    val expected = List[Constraint](
      Constraint('s', ConstraintType.Absent),
      Constraint('p', ConstraintType.Exists),
      Constraint('e', ConstraintType.Position),
      Constraint('e', ConstraintType.Exists),
      Constraint('d', ConstraintType.Absent),
    )
    res should contain theSameElementsInOrderAs expected
  }
}
