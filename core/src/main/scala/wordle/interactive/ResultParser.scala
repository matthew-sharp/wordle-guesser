package wordle.interactive

import atto.*
import atto.Atto.*
import cats.effect.implicits.{genSpawnOps, genTemporalOps, monadCancelOps}
import cats.effect.kernel.implicits.{genSpawnOps, genTemporalOps, monadCancelOps}
import cats.effect.kernel.syntax.all.{genSpawnOps, genTemporalOps, monadCancelOps}
import cats.effect.kernel.syntax.monadCancel.monadCancelOps
import cats.kernel.CommutativeMonoid
import cats.syntax.all.*
import wordle.Msg
import wordle.model.ConstraintType

object ResultParser {
  val result: Parser[Any] = string("!qq") | string("!q")
    | manyN(5, choice(ConstraintType.values.toList.map(cns => char(cns.c))))

  def parse(boardNum: Int)(input: String): Msg = {
    result.parse(input.trim)
      .done
      .either
      .flatMap {
        case "!q" => Either.left("!abort")
        case "!qq" => Either.left("!quit")
        case chars: List[Char] => chars.map(c =>
          ConstraintType.values.find(cns => cns.c == c)
            .fold(Either.left(s"'$c' not a valid result part'"))(Either.right)
        ).sequence
      }
      .map(cons => MsgInteractive.SetResult(cons, boardNum))
      .leftMap(l => l match
        case "!abort" => MsgInteractive.Abort
        case "!quit" => Msg.Quit
        case err => Msg.Invalid(err)
      )
      .merge
  }
}
