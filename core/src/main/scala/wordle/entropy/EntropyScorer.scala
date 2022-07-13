package wordle.entropy

import wordle.model.*
import wordle.util.MemoizedLog

import scala.collection.immutable.BitSet

case class EntropyScorer(resultsCache: CachedResults,
                         totalWeight: Double,
                         logTotalWeight: Double,
                         remainingValidWords: Map[Word, Double],
                        ) extends Scorer with WeightedScorer {
  private val log2 = MemoizedLog(2)
  private val totalWordCount = resultsCache.wordMapping.size

  def score(validAnswers: BitSet)(candidate: Word, guessNum: Int): Double = {
    val totalGuesses = validAnswers.size
    val totalLog = MemoizedLog(totalGuesses)
    val numByResult = validAnswers.toSeq
      .map(wordId => resultsCache.resultLookup(candidate * totalWordCount + wordId))
      .groupMapReduce(identity)(_ => 1)(_ + _).values
    val possibleAnswerBias =
      if (validAnswers.contains(candidate))
        if (totalGuesses <= 1) 100
        else log2 / MemoizedLog(totalGuesses)
      else 0
    // This is an optimisation of numByResult.map(c => c/totalGuesses * Math.log(totalGuesses/c) / log2).sum
    (totalLog - numByResult.map(c => c * MemoizedLog(c)).sum / totalGuesses) / log2 + possibleAnswerBias
  }

  def prepWeightedScoringRound(remainingValidWords: Map[Word, Double]): EntropyScorer = {
    val totalWeight = remainingValidWords.values.sum
    val logTotalWeight = Math.log(totalWeight)
    this.copy(
      totalWeight = totalWeight,
      logTotalWeight = logTotalWeight,
      remainingValidWords = remainingValidWords,
    )
  }

  def weightedScore(candidate: Word, guessNum: Int): Double = {
    val weightByResult = remainingValidWords.toSeq
      .map((wordId, weight) => (resultsCache.resultLookup(candidate * totalWordCount + wordId), weight))
      .groupMapReduce(_._1)(_._2)(_ + _).values
    val possibleAnswerBias = remainingValidWords.get(candidate) match
      case Some(weight) =>
        if (remainingValidWords.size <= 1) 100
        else weight / totalWeight
      case None => 0

    // This is an optimisation of weightByResult.map(w => w/totalWeight * Math.log(totalWeight/w) / log2).sum
    val entOfGuess = (logTotalWeight - (weightByResult.map(w => w * Math.log(w)).sum / totalWeight)) / log2
    entOfGuess + possibleAnswerBias
  }
}

object EntropyScorer {
  def apply(results: CachedResults): EntropyScorer = EntropyScorer(results, 0, 0, Map.empty[Word, Double])
}