package com.example.aap.Community
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop
import com.bumptech.glide.module.AppGlideModule
import com.example.aap.R
import com.example.aap.databinding.GalleryRowBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.storage.FirebaseStorage


class GalleryAdapter(options: FirebaseRecyclerOptions<Gallery>)
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
        val view =GalleryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Gallery) {
        val context = holder.itemView.context
        holder.binding.apply {
//            var ref =FirebaseStorage.getInstance().getReference("images").child("IMAGE_"+
//                    model.galleryNum.toString()+".jpg")

//            val ref = FirebaseStorage.getInstance().getReference().child("images/IMAGE_"+model.galleryNum+".jpg")
            val ref = FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://allaboutpet-1c6f3.appspot.com/images/IMAGE_1.jpg")
            val uri = model.galleryImage
            Glide.with(context)
                .asGif()
                .load(ref)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_photo_library_24)
                .override(300, 600)
                .fitCenter()
                .into(galleryContentImage)
            galleryNum.text = model.galleryNum.toString()
            galleryTitle.text = model.galleryTitle

        }

    }
}

