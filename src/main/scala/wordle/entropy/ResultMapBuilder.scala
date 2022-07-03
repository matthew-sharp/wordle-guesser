package wordle.entropy

import cats.effect.IO
import cats.instances.list._
import cats.syntax.parallel._
import wordle.io.WordResultReader
import wordle.util.ResultUtils

object ResultMapBuilder {
  def wordResultMap(bytes: Array[Byte]): Map[String, Short] = {
    bytes.grouped(7).map { wordBytes =>
      ResultUtils.toWordTernary(wordBytes)
    }.toMap
  }

  def readWordResultMap(word: String): IO[Map[String, Short]] = {
    WordResultReader.readWordResult(word).map(wordResultMap)
  }

  def resultMap(words: List[String]): IO[Map[String, Map[String, Short]]] = {
    words.parTraverse { w =>
      readWordResultMap(w).map((w, _))
    }.map(l => l.toMap)
  }
}
