package com.brettwalking.vords

import android.content.Context
import android.util.Log
import com.brettwalking.vords.gameDatabase.Solution
import java.util.*


class GameHandler(private val wordSize: Int) {

    private var answerList: List<String> = listOf()
    private var possibleGuesses: List<String> = listOf()
    private var answer: String = "FIRST"
    private var guessList: MutableList<String> = mutableListOf()
    private var letterBank: MutableMap<Char, Int> = mutableMapOf(
        'A' to 3,
        'B' to 3,
        'C' to 3,
        'D' to 3,
        'E' to 3,
        'F' to 3,
        'G' to 3,
        'H' to 3,
        'I' to 3,
        'J' to 3,
        'K' to 3,
        'L' to 3,
        'M' to 3,
        'N' to 3,
        'O' to 3,
        'P' to 3,
        'Q' to 3,
        'R' to 3,
        'S' to 3,
        'T' to 3,
        'U' to 3,
        'V' to 3,
        'W' to 3,
        'X' to 3,
        'Y' to 3,
        'Z' to 3
        )

    fun validate(guess : String) : Boolean {
        Log.i("validating guess", guess)
        Log.i("guess size", possibleGuesses.size.toString())
        Log.i("some word", possibleGuesses[555])
        return (guess in possibleGuesses)
    }

    fun check(guess : String) : MutableList<Int>  {
        Log.i("checking guess", guess)
        val re: MutableList<Int> = mutableListOf(0,0,0,0,0)
        if (wordSize == 6) {
            re.add(0)
        }

        for (i in 0 until wordSize) {
            if (guess[i] in answer) {
                if(guess[i] == answer[i]){
                    re[i] = 2
                }
                else {
                    re[i] = 1
                }
            }
        }

        for (i in 0 until wordSize) {
            if (re[i] == 1) {
                var gc = 0
                var ac = 0
                for (j in 0 until wordSize) {
                    if (guess[j] == guess[i] && re[j] == 2) gc += 1
                    if (answer[j] == guess[i] && re[j] == 2) ac += 1
                }
                if (gc == ac && gc > 0) re[i] = 0
            }
        }

        guessList.add(guess)

        updateLetterBank(guess, re)

        return re
    }

    fun newGame() {
        answer = answerList[Random().nextInt(answerList.size)-1]

        letterBank = mutableMapOf(
            'A' to 3,
            'B' to 3,
            'C' to 3,
            'D' to 3,
            'E' to 3,
            'F' to 3,
            'G' to 3,
            'H' to 3,
            'I' to 3,
            'J' to 3,
            'K' to 3,
            'L' to 3,
            'M' to 3,
            'N' to 3,
            'O' to 3,
            'P' to 3,
            'Q' to 3,
            'R' to 3,
            'S' to 3,
            'T' to 3,
            'U' to 3,
            'V' to 3,
            'W' to 3,
            'X' to 3,
            'Y' to 3,
            'Z' to 3
        )
    }

    fun firstGame(context: Context) {
        when (wordSize) {
            5 -> {
                possibleGuesses = context.assets.open("fiveletterwords.txt").bufferedReader().use{ it.readText() }.split(' ')
                answerList = context.assets.open("fivelettersolutions.txt").bufferedReader().use{ it.readText() }.split(' ')
            }
            6 -> {
                possibleGuesses = context.assets.open("sixletterwords.txt").bufferedReader().use{ it.readText() }.split(' ')
                answerList = context.assets.open("sixlettersolutions.txt").bufferedReader().use{ it.readText() }.split(' ')
            }
        }
        answer = answerList[Random().nextInt(answerList.size)-1]
    }

    fun getGuessList() : MutableList<String> { return guessList}

    private fun updateLetterBank(guess: String, check: List<Int>) {
        for (i in 0 until wordSize) {
            if(letterBank[guess[i]] == 3) {
                letterBank[guess[i]] = check[i]
            }
            if (check[i] == 2) {
                letterBank[guess[i]] = 2
            }
        }
    }

    fun tellLetterBank() : String {

        var solved = "Solved: "
        var known = "Known: "
        var available = "Available: "

        for (i in letterBank) {
            val addThis: String = i.key + ", "
            when (i.value) {
                2 -> {
                    solved += addThis
                }
                1 -> {
                    known += addThis
                }
                3 -> {
                    available += addThis
                }
            }
        }

        if(solved.lastIndexOf(',')!=-1) {
            solved = solved.substring(0, solved.lastIndexOf(','))
        }
        if(known.lastIndexOf(',')!=-1) {
            known = known.substring(0, known.lastIndexOf(','))
        }
        if(available.lastIndexOf(',')!=-1) {
            available = available.substring(0, available.lastIndexOf(','))
        }

        return (solved + "\n\n" + known + "\n\n" + available)
    }

    fun asSolution() : Solution {
        var str = ""
        for (i in guessList) {
            str += "$i,"
        }
        str = str.substring(0, str.length-2)
        return Solution(solution = answer, wordSize = wordSize, guessList = str, guessCount = guessList.size)
    }

}