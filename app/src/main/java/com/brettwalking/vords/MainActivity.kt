package com.brettwalking.vords

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.activity.viewModels
import com.brettwalking.vords.gameDatabase.*
import com.brettwalking.vords.history.HistoryActivity



class MainActivity : AppCompatActivity() {

    private val solutionViewModel: SolutionViewModel by viewModels {
        SolutionViewModelFactory((application as SolutionsApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    fun startFiveLetterGame(view: View) {
        val intent = Intent(this, FiveLetterGameActivity::class.java).apply {}
        startActivity(intent)
    }

    fun startSixLetterGame(view: View) {
        val intent = Intent(this, SixLetterGameActivity::class.java).apply {}
        startActivity(intent)
    }

    fun checkHistoryDatabase(view: View) {
        val intent = Intent(this, HistoryActivity::class.java).apply {}
        startActivity(intent)
    }
}