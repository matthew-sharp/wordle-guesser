package wordle.model

trait Driver {
  def go(hardMode: Boolean): (Int, String)
}
