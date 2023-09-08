package com.example.embarcados.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.embarcados.R
import com.example.embarcados.databinding.BoardConfigCardBinding
import com.example.embarcados.presentation.view_state.BoardConfigCardViewState

class BoardConfigCardAdapter(
    val onGiveAccessClick: (String) -> Unit,
    val onRemoveUserClick: (String) -> Unit
) : RecyclerView.Adapter<BoardConfigCardAdapter.ViewHolder>() {
    private var data: List<BoardConfigCardViewState> = emptyList()

    fun setData(boardList: List<BoardConfigCardViewState>){
        this.data = boardList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.board_config_card, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(boardConfigCardViewState: BoardConfigCardViewState){
            val bind = BoardConfigCardBinding.bind(itemView)
            bind.cardTitle.text = boardConfigCardViewState.name
            bind.giveAccessBtn.setOnClickListener {
                onGiveAccessClick(boardConfigCardViewState.name)
            }
            bind.removeBtn.setOnClickListener {
                onRemoveUserClick(boardConfigCardViewState.name)
            }
            bind.giveAccessBtn.isVisible = !boardConfigCardViewState.hasAccess
        }
    }
}