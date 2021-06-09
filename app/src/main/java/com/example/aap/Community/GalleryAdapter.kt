package com.example.aap.Community

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aap.databinding.BoardRowBinding
import com.example.aap.databinding.GalleryRowBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import java.util.*

class GalleryAdapter (options: FirebaseRecyclerOptions<Gallery>)
    : FirebaseRecyclerAdapter<Gallery, GalleryAdapter.ViewHolder>(options)
{

    interface OnitemClickListener{
        fun OnItemClick(view: View, position: Int)
    }
    var itemClickListener: OnitemClickListener?=null
    inner class ViewHolder(val binding: GalleryRowBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.root.setOnClickListener {
                itemClickListener!!.OnItemClick(it, adapterPosition)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =GalleryRowBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Gallery) {
        holder.binding.apply{
            galleryNum.text = model.galleryNum.toString()
            galleryTitle.text = model.galleryTitle.toString()
            var bitmap = StringToBitmap(model.galleryImage)
            galleryContetnImage.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }
    fun StringToBitmap(encodedString: String?): Bitmap? {
        return try {

            val encodeByte: ByteArray = Base64.getDecoder().decode(encodedString) // String 화 된 이미지를  base64방식으로 인코딩하여 byte배열을 만듬
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size) //만들어진 bitmap을 return
        } catch (e: Exception) {
            e.message
            null
        }
    }


}