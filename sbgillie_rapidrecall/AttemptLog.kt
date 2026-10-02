
package com.example.sbgillie_rapidrecall

/*
* Is the recorder of all the attempts that have been done while the app is running
* It stores attempt amount, amount of successes/failures and there percentage of W/L
*
* Issues include the data being lost when the program is coded*/
class AttemptLog {
    private val attempts = mutableListOf<Attempt>()
    fun addAttempt(attempt: Attempt) {
        attempts.add(attempt)
    }
    fun getAttempts(): List<Attempt> {
        return attempts.toList()
    }
    fun getTotalAttempts(): Int {
        return attempts.size
    }

    fun getCorrectAttempts(): Int {
        return attempts.count { it.correct }
    }

    fun getIncorrectAttempts(): Int {
        return getTotalAttempts() - getCorrectAttempts()
    }

    fun getAccuracy(): Double {
        if (attempts.isEmpty()) {
            return 0.0
        }
        return getCorrectAttempts().toDouble() /
                getTotalAttempts().toDouble() * 100.0
    }
}