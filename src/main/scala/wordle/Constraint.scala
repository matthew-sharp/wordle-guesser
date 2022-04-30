package wordle

case class Constraint (
                        c: Char,
                        constraintType: ConstraintType,
                      )

sealed trait ConstraintType

object ConstraintType {
  case object Position extends ConstraintType
  case object Exists extends ConstraintType
  case object Absent extends ConstraintType
}