package wordle.util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.TableFor2

import wordle.util.MeanExtensions._

class MeanExtensionsSpec extends AnyFlatSpec with should.Matchers {
  val tests: TableFor2[Seq[Int], Int] = Table(
    ("elems", "mean"),
    (Seq(2, 8), 4),
    (Seq(1, 2, 3, 4, 5, 6, 7, 8), 4),
    (Seq(15, 12, 13, 19, 10), 13),
  )
  forAll(tests) { (elems: Seq[Int], mean: Int) =>
    Math.round(MeanExtensions(elems).geoMean) shouldBe mean
  }
}
