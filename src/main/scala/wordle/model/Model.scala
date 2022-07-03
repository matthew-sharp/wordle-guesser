package wordle.model

case class Model(
                wordlist: Set[String],
                resultMap: Map[String, Map[String, Short]],
                )
