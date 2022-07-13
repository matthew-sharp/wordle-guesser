package wordle.util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import wordle.model.{Constraint, ConstraintType}
import wordle.util.ResultUtils.*

class ResultUtilsSpec extends AnyFlatSpec with should.Matchers {
  "ResultUtils.ternary" should "correctly handle a single green" in {
    val word = "c"
    val cons = List(Constraint('c', ConstraintType.Position))

    val res = toConstraints(toTernary(cons.map(_.constraintType)), word)

    res should contain theSameElementsInOrderAs cons
  }

  it should "correctly handle a yellow and a black" in {
    val word = "ca"
    val cons = List(
      Constraint('c', ConstraintType.Exists),
      Constraint('a', ConstraintType.Absent)
    )

    val res = toConstraints(toTernary(cons.map(_.constraintType)), word)

    res should contain theSameElementsInOrderAs cons
  }

  it should "correctly handle a word starting with a black" in {
    val word = "ca"
    val cons = List(
      Constraint('c', ConstraintType.Absent),
      Constraint('a', ConstraintType.Exists)
    )

    val res = toConstraints(toTernary(cons.map(_.constraintType)), word)

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

    val res = toConstraints(toTernary(cons.map(_.constraintType)), word)

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

    val res = toConstraints(toTernary(cons.map(_.constraintType)), word)

    res.size shouldEqual 5
    res should contain theSameElementsInOrderAs cons
  }
}
