package wordle.model

case class Constraint (
                        c: Char,
                        constraintType: ConstraintType,
                      )

sealed trait ConstraintType(val str: String)

object ConstraintType {
  case object Position extends ConstraintType("\u001b[32mg")
  case object Exists extends ConstraintType("\u001b[33my")
  case object Absent extends ConstraintType("\u001b[30mb")
}