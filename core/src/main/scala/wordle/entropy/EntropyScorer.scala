package wordle.entropy

import wordle.model.*
import wordle.util.MemoizedLog

case class EntropyScorer(resultsCache: CachedResults,
                         totalWeight: Double,
                         logTotalWeight: Double,
                         startingEntropy: Double,
                         remainingValidWords: IArray[(Word, Double)],
                        ) extends Scorer with WeightedScorer {
  private val log2 = MemoizedLog(2)
  /**
   * Tuning this value higher will make the scorer favour trying to guess the answer rather than look for entropy
   * Tuning this value lower will make the scorer favour more information until it is certain of the answer
   */
  inline private val entropyPerGuessFactor = 13.0 / 4

  inline def flatScore(validAnswers: IArray[Word])(candidate: Word, guessNum: Int): ScoreInfo = {
    val totalGuesses = validAnswers.size
    val totalLog = MemoizedLog(totalGuesses)
    val numByResult = validAnswers
      .map(wordId => resultsCache.getResult(candidate, wordId))
      .groupMapReduce(identity)(_ => 1)(_ + _).values
    val probIsAnswer =
      if (validAnswers.contains(candidate)) 1.0 / totalGuesses
      else 0
    // This is an optimisation of numByResult.map(c => c/totalGuesses * Math.log(totalGuesses/c) / log2).sum
    val entropy = (totalLog - numByResult.map(c => c * MemoizedLog(c)).sum / totalGuesses) / log2
    ScoreInfo(
      probability = probIsAnswer,
      rawScore = entropy,
      estimatedRemainingEffort = 1 + (totalLog / log2 - entropy) / entropyPerGuessFactor)
  }

  def prepWeightedScoringRound(remainingValidWords: IArray[(Word, Double)]): EntropyScorer = {
    val totalWeight = remainingValidWords.map(_._2).sum
    val logTotalWeight = Math.log(totalWeight)
    val startingEntropy = (logTotalWeight - (remainingValidWords.map((_, w) => w * Math.log(w)).sum / totalWeight)) / log2
    this.copy(
      totalWeight = totalWeight,
      logTotalWeight = logTotalWeight,
      startingEntropy = startingEntropy,
      remainingValidWords = IArray.from(remainingValidWords),
    )
  }

  inline def weightedScore(candidate: Word, guessNum: Int): ScoreInfo = {
    val weightByResult = remainingValidWords
      .map((wordId, weight) => (resultsCache.getResult(candidate, wordId), weight))
      .groupMapReduce(_._1)(_._2)(_ + _).values
    val probIsAnswer = remainingValidWords.find(_._1 == candidate) match
      case Some((_, weight)) =>
        if (remainingValidWords.size <= 1) 1
        else weight / totalWeight
      case None => 0

    // This is an optimisation of weightByResult.map(w => w/totalWeight * Math.log(totalWeight/w) / log2).sum
    val entOfGuess = (logTotalWeight - (weightByResult.map(w => w * Math.log(w)).sum / totalWeight)) / log2
    ScoreInfo(
      probability = probIsAnswer,
      rawScore = entOfGuess,
      estimatedRemainingEffort = 1 + (startingEntropy - entOfGuess) / entropyPerGuessFactor)
  }
}

object EntropyScorer {
  def apply(results: CachedResults): EntropyScorer = EntropyScorer(results, 0, 0, 0, IArray.empty[(Word, Double)])
}