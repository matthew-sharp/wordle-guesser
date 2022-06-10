package wordle

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class WordPrunerSpec extends AnyFlatSpec with should.Matchers {
  "WordPruner.pruneWords" should "correctly filter out steal when the guess is speed" in {
    val cons = List[Constraint](
      Constraint('s', ConstraintType.Position),
      Constraint('p', ConstraintType.Absent),
      Constraint('e', ConstraintType.Absent),
      Constraint('e', ConstraintType.Position),
      Constraint('d', ConstraintType.Absent),
    )
    val res = WordPruner.pruneWords(Set("steal"), cons)

    res.size shouldEqual 0
  }

  it should "correctly keep steal when the answer is speed" in {
    val cons = List[Constraint](
      Constraint('s', ConstraintType.Position),
      Constraint('p', ConstraintType.Absent),
      Constraint('e', ConstraintType.Position),
      Constraint('e', ConstraintType.Absent),
      Constraint('d', ConstraintType.Absent),
    )
    val res = WordPruner.pruneWords(Set("steal"), cons)

    res.size shouldEqual 1
  }
}
