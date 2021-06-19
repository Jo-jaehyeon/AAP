package com.example.aap.PetInfo

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aap.R
import java.io.File

class AlbumAdapter (val items: ArrayList<AlbumData>) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        //petrow.xml
        val date: TextView = itemView.findViewById(R.id.dateView)
        val image: ImageView = itemView.findViewById(R.id.AlbumImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        //row.xml이 들어옴
        val view = LayoutInflater.from(parent.context).inflate(R.layout.petrow, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.date.text = items[position].date
        val file = File(items[position].path)
        if(file.exists())
        {
            val bitmap = BitmapFactory.decodeFile(items[position].path)
            holder.image.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int
    {
        return items.size
    }
}