package wordle.model

case class Constraint (
                        c: Char,
                        constraintType: ConstraintType,
                      )

sealed trait ConstraintType {
  val c: Char
}

object ConstraintType {
  case object Position extends ConstraintType{ val c = 'g' }
  case object Exists extends ConstraintType { val c = 'y' }
  case object Absent extends ConstraintType { val c = 'b' }
}