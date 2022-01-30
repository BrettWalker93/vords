package com.brettwalking.vords.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brettwalking.vords.R
import com.brettwalking.vords.gameDatabase.*
import kotlinx.coroutines.flow.Flow

class HistoryActivity : AppCompatActivity() {

    private val solutionViewModel: SolutionViewModel by viewModels {
        SolutionViewModelFactory((application as SolutionsApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

    }

    fun fiveHistory(view: View) {
        Log.i("loc", "history activity fivehistory fun")
        solutionViewModel.updateList(5)
    }

    fun sixHistory(view: View) {
        Log.i("loc", "history activity sixhistory fun")
        solutionViewModel.updateList(6)
    }
}