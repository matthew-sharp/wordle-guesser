package wordle.io

import cats.effect.IO

object WordlistReader extends FileLineReaderWithResourceDefault("wordlist")
