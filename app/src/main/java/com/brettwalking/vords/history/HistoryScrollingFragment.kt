package com.brettwalking.vords.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brettwalking.vords.R
import com.brettwalking.vords.gameDatabase.*

class HistoryScrollingFragment : Fragment() {

    private val solutionViewModel: SolutionViewModel by activityViewModels {
        SolutionViewModelFactory((activity?.application as SolutionsApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history_scrolling, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = requireActivity().findViewById<RecyclerView>(R.id.historyscrollingrecyclerview)
        val adapter = SolutionListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        solutionViewModel.allSolutions.observe(viewLifecycleOwner) {
            Log.i("loc", "historysrollingfrag all solutions observe")
            solutionViewModel.updateList(5)
        }

        solutionViewModel.currentList.observe(viewLifecycleOwner) { solutions ->
            // Update the cached copy of the solutions in the adapter.
            Log.i("loc", "historysrollingfrag current list observe")
            solutions.let {
                adapter.submitList(it) }
        }
    }
}