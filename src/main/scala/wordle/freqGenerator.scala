package wordle

import java.io.{FileOutputStream, ObjectOutputStream}
import scala.io.Source

object freqGenerator extends App {
  val words = Source.fromFile("wordlist").getLines().toList
}
