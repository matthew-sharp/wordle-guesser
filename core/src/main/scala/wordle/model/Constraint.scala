package wordle.model

case class Constraint(
                       c: Char,
                       constraintType: ConstraintType,
                     )

enum ConstraintType(val c: Char):
  case Absent extends ConstraintType('b')
  case Exists extends ConstraintType('y')
  case Position extends ConstraintType('g')
