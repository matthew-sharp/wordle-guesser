package wordle.model

import wordle.Msg

trait ConsoleType

case object Generic extends ConsoleType

case class Console(
                    outputMsg: String,
                    prompt: String,
                    parseCallback: String => Msg,
                    conType: ConsoleType = Generic,
                  )


extension (m: Model[_]) {
  def setOutputMsgIfNotBatch(outMsg: String): Model[_] = {
    if m.batchMode then m
    else {
      val newTopCon = m.consoles.head.copy(outputMsg = outMsg)
      m.copy(consoles = newTopCon :: m.consoles.tail)
    }
  }
  
  def setOutputMsgEvenIfBatch(outMsg: String): Model[_] = {
    val newTopCon = m.consoles.head.copy(outputMsg = outMsg)
    m.copy(consoles = newTopCon :: m.consoles.tail)
  }

  def pushConsole(c: Console): Model[_] = {
    m.copy(consoles = c :: m.consoles)
  }

  def pushConsoles(cs: Iterable[Console]): Model[_] = {
    m.copy(consoles = cs ++: m.consoles)
  }

  def popConsole: Model[_] = {
    m.copy(consoles = m.consoles.tail)
  }
}
