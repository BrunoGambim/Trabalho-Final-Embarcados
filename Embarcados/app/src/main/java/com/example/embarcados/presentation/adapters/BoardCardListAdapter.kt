package com.example.embarcados.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.embarcados.R
import com.example.embarcados.databinding.BoardCardBinding
import com.example.embarcados.presentation.view_state.BoardCardViewState

class BoardCardListAdapter(val onBoardConfigClick: (String) -> Unit,
                           val onOpenClick: (String) -> Unit,
                           val onCloseClick: (String) -> Unit) : RecyclerView.Adapter<BoardCardListAdapter.ViewHolder>() {

    private var data: List<BoardCardViewState> = emptyList()

    fun setData(boardList: List<BoardCardViewState>){
        this.data = boardList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.board_card, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(boardCardViewState: BoardCardViewState){
            val bind = BoardCardBinding.bind(itemView)
            bind.cardTitle.text = boardCardViewState.name
            bind.optionsBtn.setOnClickListener {
                onBoardConfigClick(boardCardViewState.name)
            }
            bind.openBtn.setOnClickListener {
                onOpenClick(boardCardViewState.name)
            }
            bind.closeBtn.setOnClickListener {
                onCloseClick(boardCardViewState.name)
            }
            bind.optionsBtn.isVisible = boardCardViewState.isAdmin

            bind.closeBtn.isEnabled = boardCardViewState.hasAccess
            bind.openBtn.isEnabled = boardCardViewState.hasAccess
        }
    }
}