package wordle.model

case class Model(
                outputMsg: String,
                wordlist: Set[String],
                resultMap: Map[String, Map[String, Short]],
                )
