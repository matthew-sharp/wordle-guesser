package wordle.model

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

object ResultUtils {
  def toTernary(results: Seq[ConstraintType]): Short = {
    results match {
      case Nil => 1
      case head :: tail =>
        val trit: Short = head match {
          case ConstraintType.Absent => 0
          case ConstraintType.Exists => 1
          case ConstraintType.Position => 2
        }
        (3 * toTernary(tail) + trit).toShort
    }
  }

  def toConstraints(ternary: Short, word: String): List[Constraint] = {
    val trit = ternary % 3
    val conType = trit match {
      case 0 => ConstraintType.Absent
      case 1 => ConstraintType.Exists
      case 2 => ConstraintType.Position
    }
    val remainingTernary = (ternary / 3).toShort
    if (remainingTernary > 0)
      Constraint(word.head, conType) :: toConstraints(remainingTernary, word.tail)
    else
      Nil
  }

  def toBytes(word: String, results: Seq[ConstraintType]): Array[Byte] = {
    val ternary = toTernary(results)
    val bb = ByteBuffer.allocate(7)
    bb.put(word.getBytes(StandardCharsets.UTF_8))
    bb.putShort(ternary)
    bb.array()
  }

  def toWordTernary(bytes: Array[Byte]): (String, Short) = {
    val word = new String(bytes.take(5), StandardCharsets.UTF_8)
    val ternary = ByteBuffer.wrap(bytes.drop(5)).getShort
    (word, ternary)
  }

  def toConstraints(bytes: Array[Byte]): List[Constraint] = {
    val (word, ternary) = toWordTernary(bytes)
    toConstraints(ternary, word)
  }
}
