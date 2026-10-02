package com.example.sbgillie_rapidrecall
/*
* Generates the random digits for the game engine
*
* Issues: It's disconnected from MainActivity so can be more inconvenient for testing
* */
class SequenceGenerator {

    fun generate(length: Int): String {
        require(length in 1..10)

        return buildString {
            repeat(length) {
                append((0..9).random())
            }
        }
    }
}