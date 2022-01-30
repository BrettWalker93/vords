package com.brettwalking.vords.gameDatabase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.brettwalking.vords.R

class SolutionListAdapter : ListAdapter<Solution, SolutionListAdapter.SolutionViewHolder>(SolutionsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolutionViewHolder {
        return SolutionViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: SolutionViewHolder, position: Int) {
        val current = getItem(position)

        val text = current.solution + " - " + current.guessCount.toString()
        holder.bindSol(text)
        //holder.bindCount(current.guessCount.toString())
    }

    class SolutionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val solutionItemView: TextView = itemView.findViewById(R.id.recyclerview_item_text_sol)
        //private val solutionItemView2: TextView = itemView.findViewById(R.id.recyclerview_item_text_count)

        fun bindSol(text: String?) {
            solutionItemView.text = text
        }
        /*
        fun bindCount(text: String?) {
            solutionItemView2.text = text
        }
        */
        companion object {
            fun create(parent: ViewGroup): SolutionViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return SolutionViewHolder(view)
            }
        }

    }

    class SolutionsComparator : DiffUtil.ItemCallback<Solution>() {
        override fun areItemsTheSame(oldItem: Solution, newItem: Solution): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Solution, newItem: Solution): Boolean {
            return oldItem.solution == newItem.solution
        }
    }
}
