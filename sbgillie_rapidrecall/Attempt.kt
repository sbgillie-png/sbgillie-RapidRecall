package com.example.sbgillie_rapidrecall
data class Attempt(
    /*
    * This is a single attempt of the rapid Recall game
    * it stores sequenceLength,userInput, target Sequence, weither or not its correct and a timestamped date
    *
    * Issues are it doesn't permanently stay stored in data*/
    val sequenceLength: Int,
    val userInput: String,
    val targetSequence: String,
    val correct: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

