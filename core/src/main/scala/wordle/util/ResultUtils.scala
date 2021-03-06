package wordle.util

import wordle.model.*

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import scala.annotation.targetName

object ResultUtils {
  def toTernary(results: List[ConstraintType]): ResultTernary = {
    results match {
      case Nil => 0.asInstanceOf[ResultTernary]
      case head :: tail =>
        val trit = head match {
          case ConstraintType.Absent => 0
          case ConstraintType.Exists => 1
          case ConstraintType.Position => 2
        }
        (3 * toTernary(tail) + trit).toByte
    }
  }

  @targetName("con2Ternary")
  inline def toTernary(results: List[Constraint]): ResultTernary = {
    toTernary(results.map(_.constraintType))
  }

  inline def toResultString(results: Seq[ConstraintType]): String = {
    results.map(_.c).mkString
  }

  def toConstraintTypes(ternary: ResultTernary): List[ConstraintType] = {
    def until(ternary: ResultTernary, remaining: Int): List[ConstraintType] = {
      if (remaining <= 0) Nil
      else {
        val posInt = ternary.asInstanceOf[Int] & 0xff
        val trit = posInt % 3
        val conType = trit match {
          case 0 => ConstraintType.Absent
          case 1 => ConstraintType.Exists
          case 2 => ConstraintType.Position
        }
        val remainingTernary = (posInt / 3).toByte
        conType :: until(remainingTernary, remaining - 1)
      }
    }

    until(ternary, 5)
  }

  def toConstraints(ternary: ResultTernary, word: String): List[Constraint] = {
    val posInt = ternary.asInstanceOf[Int] & 0xff
    val trit = posInt % 3
    val conType = trit match {
      case 0 => ConstraintType.Absent
      case 1 => ConstraintType.Exists
      case 2 => ConstraintType.Position
    }
    val remainingTernary = (posInt / 3).toByte
    if (word.nonEmpty)
      Constraint(word.head, conType) :: toConstraints(remainingTernary, word.tail)
    else
      Nil
  }
}
