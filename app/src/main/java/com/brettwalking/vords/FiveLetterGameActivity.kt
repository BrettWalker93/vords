package com.brettwalking.vords

import android.app.ActionBar
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import java.util.*
import android.util.TypedValue
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import com.brettwalking.vords.gameDatabase.Solution
import com.brettwalking.vords.gameDatabase.SolutionViewModel
import com.brettwalking.vords.gameDatabase.SolutionViewModelFactory
import com.brettwalking.vords.gameDatabase.SolutionsApplication


/*  TODO
        move to next letter automatically
 */


class FiveLetterGameActivity : AppCompatActivity() {

    private val solutionViewModel: SolutionViewModel by viewModels {
        SolutionViewModelFactory((application as SolutionsApplication).repository)
    }

    private val game = GameHandler(5)
    var guessList: MutableList<EditText> = mutableListOf()
    var guessCount = 0

    private lateinit var paramRef: ConstraintLayout.LayoutParams

    var newGame = false

    private var filter =
        InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (!Character.isLetter(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_five_letter_game)

        //reference value for later
        val ref = findViewById<TextView>(R.id.guessReference)
        paramRef = ref.layoutParams as ConstraintLayout.LayoutParams
        ref.isEnabled = false
        ref.isVisible = false

        val submit = findViewById<Button>(R.id.submitButton)
        submit.setOnClickListener {
            if (!newGame) tryGuess()
            else startOver()
        }

        val letterBank = findViewById<Button>(R.id.letterBankButton)
        letterBank.setOnClickListener {
            showLetterBank()
        }

        gameStart()
    }

    private fun startOver() {
        newGame = false
        guessCount = 0

        val submit = findViewById<Button>(R.id.submitButton)
        submit.text = getString(R.string.submit)

        val rootLayout: ConstraintLayout = findViewById(R.id.scrollViewConstraint)

        val constraintSet = ConstraintSet()

        val ht = submit.marginTop

        //reattach submit to top line
        with(constraintSet) {
            clone(rootLayout)
            clear(
                R.id.submitButton,
                ConstraintSet.TOP
            )
            connect(
                R.id.submitButton,
                ConstraintSet.TOP,
                guessList[4].id,
                ConstraintSet.BOTTOM,
                ht
            )
            applyTo(rootLayout)
        }

        //remove 2nd thru last guess lines
        while (guessList.size > 5) {
            rootLayout.removeView(guessList.last())
            guessList.removeAt(guessList.size - 1)
        }

        for (i in guessList) {
            i.isEnabled = true
            i.text.clear()
            i.setBackgroundColor(Color.LTGRAY)
        }
        guessList[0].requestFocus()
        System.gc()
        game.newGame()
    }

    private fun tryGuess() {

        val guess = constructGuessFromViews().uppercase()
        if (game.validate(guess)) {
            val check = game.check(guess)

            var checksum = 0
            for (i in check) checksum += i

            if (checksum == 10) {
                sendToDatabase()
                for (i in 0..4) {
                    val j = i + guessCount * 5
                    guessList[j].setBackgroundColor(Color.GREEN)
                    newGame = true

                    guessList[j].isEnabled = false
                }
                val submit = findViewById<Button>(R.id.submitButton)
                submit.text = getString(R.string.newgame)
                submit.requestFocus()
            }
            else {
                Log.i("Yo", check.toString())
                for (i in 0..4) {
                    val j = i + guessCount * 5

                    val s = guessList[j].toString()
                    if (check[i] == 1) {
                        //Log.i("Yo", "changing $s to yellow")
                            /*
                        var textDrawable: Drawable? = curGuess[i].background
                        textDrawable = DrawableCompat.wrap(textDrawable!!)
                        DrawableCompat.setTint(textDrawable, Color.YELLOW)
                        curGuess[i].background = textDrawable
                             */
                        guessList[j].setBackgroundColor(Color.YELLOW)
                    }
                    else if (check[i] == 2) {
                        //Log.i("Yo", "changing $s to green")
                        guessList[j].setBackgroundColor(Color.GREEN)

                    }
                    guessList[j].isEnabled = false
                }

                val firstLetter = guessList[5*guessCount]
                guessCount += 1
                addGuess(firstLetter)
                guessList[guessList.lastIndex-4].requestFocus()
            }
        }
        else {
            val toast = Toast.makeText(this, "Invalid word", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER_VERTICAL,0,0)
            toast.show()
        }
    }

    private fun constructGuessFromViews(): String {
        var myString = ""
        for (i in 0..4) {
            val j = i + 5*guessCount
            myString += guessList[j].text
        }
        return myString
    }

    private fun addGuess(roofview: View) {
        Log.i("Yo", "adding guesses")
        val rootLayout: ConstraintLayout = findViewById(R.id.scrollViewConstraint)
        val constraintSet = ConstraintSet()

        //text boxes for guess entry
        for (i in 0..4) {
            val j = i + guessCount * 5
            val guessText = EditText(this)
            guessText.id = View.generateViewId()
            rootLayout.addView(guessText)
            guessText.setBackgroundColor(Color.LTGRAY)
            guessText.setTextColor(Color.BLACK)
            guessText.textSize = 24f
            guessText.gravity = Gravity.CENTER
            guessText.maxLines = 1
            guessText.filters = arrayOf(InputFilter.LengthFilter(1), InputFilter.AllCaps(), filter)

            //connect first button to previous guesses
            if ((j%5) == 0) {
                //connect new buttons to layout
                Log.i("Yo", "first box")
                with(constraintSet) {
                    clone(rootLayout)

                    connect(
                        guessText.id,
                        ConstraintSet.TOP,
                        roofview.id,
                        ConstraintSet.BOTTOM,
                        paramRef.topMargin
                    )
                    connect(
                        guessText.id,
                        ConstraintSet.LEFT,
                        roofview.id,
                        ConstraintSet.LEFT,
                        0
                    )
                    connect(
                        guessText.id,
                        ConstraintSet.RIGHT,
                        roofview.id,
                        ConstraintSet.RIGHT,
                        0
                    )
                    constrainHeight(
                        guessText.id,
                        paramRef.height
                    )
                    constrainWidth(
                        guessText.id,
                        paramRef.width
                    )
                    applyTo(rootLayout)
                }
            }
            //connect rest of buttons to current line
            else {
                //connect new button to layout
                Log.i("Yo", "2-4 boxes")
                with(constraintSet) {
                    clone(rootLayout)

                    connect(
                        guessText.id,
                        ConstraintSet.LEFT,
                        guessList[j-1].id,
                        ConstraintSet.RIGHT,
                        paramRef.topMargin
                    )
                    connect(
                        guessText.id,
                        ConstraintSet.BOTTOM,
                        guessList[j-1].id,
                        ConstraintSet.BOTTOM,
                        0
                    )
                    constrainHeight(
                        guessText.id,
                        paramRef.height
                    )
                    constrainWidth(
                        guessText.id,
                        paramRef.width
                    )
                    applyTo(rootLayout)
                }
            }

            guessList.add(guessText)
        }

        with (constraintSet) {
            clone(rootLayout)

            val ht = findViewById<Button>(R.id.submitButton).marginTop

            clear(
                R.id.submitButton,
                ConstraintSet.TOP
            )
            connect(
                R.id.submitButton,
                ConstraintSet.TOP,
                guessList.last().id,
                ConstraintSet.BOTTOM,
                ht
            )
            applyTo(rootLayout)
        }

        prepareGuessesForInput()

    }

    private fun gameStart() {

        game.firstGame(this)

        guessList.add(findViewById<EditText>(R.id.guess1))
        guessList.add(findViewById<EditText>(R.id.guess2))
        guessList.add(findViewById<EditText>(R.id.guess3))
        guessList.add(findViewById<EditText>(R.id.guess4))
        guessList.add(findViewById<EditText>(R.id.guess5))

        prepareGuessesForInput()
    }

    private fun showLetterBank() {
        val builder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(it)
        }

        builder.setMessage(game.tellLetterBank())
            .setTitle(R.string.letterBank)

        builder.create()
        builder.show()
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKeyboard(view: View) {
        val inputMethodManager: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, 0)
    }

    private fun prepareGuessesForInput() {

        for (i in guessList.lastIndex downTo guessList.lastIndex-4) {
            val view = guessList[i]

            //set filters
            view.filters = arrayOf(InputFilter.LengthFilter(1), InputFilter.AllCaps(), filter)

            //highlight text when edittext is entered
            view.setSelectAllOnFocus(true)

            //set listeners
            //enter
            view.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                    findViewById<Button>(R.id.submitButton).performClick()
                    return@OnEditorActionListener true
                } else if (actionId == EditorInfo.IME_ACTION_PREVIOUS) {
                    findViewById<Button>(R.id.submitButton).clearFocus()
                    return@OnEditorActionListener true
                }
                false
            })

            view.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) { //enter
                    findViewById<Button>(R.id.submitButton).performClick()
                } else if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) { //backspace
                    if(view.text.isEmpty()) {
                        if (i % 5 != 0) {
                            guessList[i-1].requestFocus()
                            guessList[i-1].text.clear()
                        }
                    }
                }
                return@OnKeyListener true
            })

            //move to next box if letter entered
            view.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(count == 1 && i < guessList.lastIndex) {
                        guessList[i+1].requestFocus()
                    }
                }
            })

            //hide keyboard when not focused
            view.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    hideKeyboard(v)
                }
                else {
                    showKeyboard(v)
                    view.append("")
                }
            }

            //go to next

        }
        guessList[guessList.lastIndex-4].requestFocus()
    }

    private fun sendToDatabase() {
        solutionViewModel.insert(game.asSolution())
    }
}