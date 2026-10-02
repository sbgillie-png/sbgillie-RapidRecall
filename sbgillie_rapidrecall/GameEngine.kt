package com.example.sbgillie_rapidrecall


/*This maintains the logic of the game
gameEngine calls sequenceGenerator for the coordination of the numbers that will be displayed and organizes all the attempts
Issues include, local data is only kept in local memory
*
* */
class GameEngine {

    private val sequenceGenerator = SequenceGenerator()
    val attemptLog = AttemptLog()
    var targetSequence: String = ""
        private set
    var sequenceLength: Int = 3
        private set

    var lastAttempt: Attempt? = null
        private set

    fun startGame(length: Int) {
        require(length in 1..10)

        sequenceLength = length
        targetSequence = sequenceGenerator.generate(length)
        lastAttempt = null
    }

    fun submitGuess(userInput: String): Attempt {
        check(targetSequence.isNotEmpty())

        val cleanedInput = userInput.trim()
        val isCorrect = cleanedInput == targetSequence

        val attempt = Attempt(
            sequenceLength = sequenceLength,
            userInput = cleanedInput,
            targetSequence = targetSequence,
            correct = isCorrect
        )

        attemptLog.addAttempt(attempt)
        lastAttempt = attempt

        return attempt
    }
}