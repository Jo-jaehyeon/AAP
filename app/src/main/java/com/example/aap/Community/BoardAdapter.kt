package com.example.aap.Community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aap.databinding.BoardRowBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class BoardAdapter (options: FirebaseRecyclerOptions<Board>)
    : FirebaseRecyclerAdapter<Board, BoardAdapter.ViewHolder>(options)
{

    interface OnitemClickListener{
        fun OnItemClick(view: View, position: Int)
    }
    var itemClickListener: OnitemClickListener?=null
    inner class ViewHolder(val binding: BoardRowBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.root.setOnClickListener {
                itemClickListener!!.OnItemClick(it, adapterPosition)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = BoardRowBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Board) {
        holder.binding.apply{
            boardnum.text = model.boardNum.toString()
            boardtitle.text = model.boardTitle.toString()
            uid.text = model.uid.toString()
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }


}