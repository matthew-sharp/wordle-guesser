package wordle.model

import wordle.Msg

case class Console(
                    outputMsg: String,
                    prompt: String,
                    parseCallback: String => Msg,
                  )

extension (m: Model) {
  def setOutputMsg(outMsg: String): Model = {
    val newTopCon = m.consoles.head.copy(outputMsg = outMsg)
    m.copy(consoles = newTopCon :: m.consoles.tail)
  }

  def pushConsole(c: Console): Model = {
    m.copy(consoles = c :: m.consoles)
  }

  def pushConsoles(cs: Iterable[Console]): Model = {
    m.copy(consoles = cs ++: m.consoles)
  }

  def popConsole: Model = {
    m.copy(consoles = m.consoles.tail)
  }
}
