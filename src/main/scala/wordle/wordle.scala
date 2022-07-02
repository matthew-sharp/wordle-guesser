package wordle

import cats.effect.unsafe.implicits.global
import entropy._

object wordle extends App {
  EntropyInteractiveApp.run(List("--ask-guess-word")).unsafeRunSync()
}
