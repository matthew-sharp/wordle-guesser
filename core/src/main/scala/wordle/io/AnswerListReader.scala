package wordle.io

import cats.effect.IO

object AnswerListReader extends FileLineReaderWithResourceDefault("answer-list")
