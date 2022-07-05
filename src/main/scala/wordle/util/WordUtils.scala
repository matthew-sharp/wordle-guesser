package wordle.util

import wordle.model.Word

object WordUtils {
  implicit class WordExtensions(val w: Word) {

  }

  def inverseWordMap(words: Seq[String]): Map[String, Word] = {
    words.zipWithIndex.toMap
  }
}
