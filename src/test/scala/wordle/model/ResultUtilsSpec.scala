package wordle.model

import ResultUtils._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ResultUtilsSpec extends AnyFlatSpec with should.Matchers {
  "ResultUtils.ternary" should "correctly handle a single green" in {
    val word = "c"
    val cons = List(Constraint('c', ConstraintType.Position))

    val res = toConstraints(toTernary(cons), word)

    res should contain theSameElementsInOrderAs cons
  }

  it should "correctly handle a yellow and a black" in {
    val word = "ca"
    val cons = List(
      Constraint('c', ConstraintType.Exists),
      Constraint('a', ConstraintType.Absent)
    )

    val res = toConstraints(toTernary(cons), word)

    res should contain theSameElementsInOrderAs cons
  }

  it should "correctly handle a word starting with a black" in {
    val word = "ca"
    val cons = List(
      Constraint('c', ConstraintType.Absent),
      Constraint('a', ConstraintType.Exists)
    )

    val res = toConstraints(toTernary(cons), word)

    res should contain theSameElementsInOrderAs cons
  }

  it should "correctly handle 5 blacks" in {
    val word = "cares"
    val cons = List(
      Constraint('c', ConstraintType.Absent),
      Constraint('a', ConstraintType.Absent),
      Constraint('r', ConstraintType.Absent),
      Constraint('e', ConstraintType.Absent),
      Constraint('s', ConstraintType.Absent),
    )

    val res = toConstraints(toTernary(cons), word)

    res.size shouldEqual 5
    res should contain theSameElementsInOrderAs cons
  }

  it should "correctly handle 5 greens" in {
    val word = "cares"
    val cons = List(
      Constraint('c', ConstraintType.Position),
      Constraint('a', ConstraintType.Position),
      Constraint('r', ConstraintType.Position),
      Constraint('e', ConstraintType.Position),
      Constraint('s', ConstraintType.Position),
    )

    val res = toConstraints(toTernary(cons), word)

    res.size shouldEqual 5
    res should contain theSameElementsInOrderAs cons
  }

  "ResultUtils.bytes" should "correctly handle a 5 letter word" in {
    val cons = List(
      Constraint('a', ConstraintType.Exists),
      Constraint('r', ConstraintType.Position),
      Constraint('r', ConstraintType.Absent),
      Constraint('a', ConstraintType.Position),
      Constraint('y', ConstraintType.Exists),
    )

    val res = toConstraints(toBytes(cons))

    res.size shouldEqual 5
    res should contain theSameElementsInOrderAs cons
  }
}
