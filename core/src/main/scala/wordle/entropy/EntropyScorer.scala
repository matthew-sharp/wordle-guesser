package wordle.entropy

import wordle.model.*
import wordle.util.MemoizedLog

import scala.collection.immutable.BitSet

case class EntropyScorer(resultsCache: CachedResults,
                         totalWeight: Double,
                         logTotalWeight: Double,
                         startingEntropy: Double,
                         remainingValidWords: Map[Word, Double],
                        ) extends Scorer with WeightedScorer {
  private val log2 = MemoizedLog(2)
  inline private val entropyPerGuessFactor = 12.6 / 3

  inline def score(validAnswers: BitSet)(candidate: Word, guessNum: Int): ScoreInfo = {
    val totalGuesses = validAnswers.size
    val totalLog = MemoizedLog(totalGuesses)
    val numByResult = validAnswers.toSeq
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

  def prepWeightedScoringRound(remainingValidWords: Map[Word, Double]): EntropyScorer = {
    val totalWeight = remainingValidWords.values.sum
    val logTotalWeight = Math.log(totalWeight)
    val startingEntropy = (logTotalWeight - (remainingValidWords.values.map(w => w * Math.log(w)).sum / totalWeight)) / log2
    this.copy(
      totalWeight = totalWeight,
      logTotalWeight = logTotalWeight,
      startingEntropy = startingEntropy,
      remainingValidWords = remainingValidWords,
    )
  }

  inline def weightedScore(candidate: Word, guessNum: Int): ScoreInfo = {
    val weightByResult = remainingValidWords.toSeq
      .map((wordId, weight) => (resultsCache.getResult(candidate, wordId), weight))
      .groupMapReduce(_._1)(_._2)(_ + _).values
    val probIsAnswer = remainingValidWords.get(candidate) match
      case Some(weight) =>
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
  def apply(results: CachedResults): EntropyScorer = EntropyScorer(results, 0, 0, 0, Map.empty[Word, Double])
}