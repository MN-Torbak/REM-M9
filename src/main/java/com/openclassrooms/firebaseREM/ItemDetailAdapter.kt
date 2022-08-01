package com.openclassrooms.firebaseREM

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.firebaseREM.viewmodel.MainViewModel

class ItemDetailAdapter(pPhotoProperty: List<Element>?) :
    RecyclerView.Adapter<ItemDetailAdapter.ItemDetailViewHolder>() {
    class ItemDetailViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var mImageView: ImageView

        init {
            mImageView = itemView.findViewById(R.id.photo_property)
        }
    }

    val mElement: List<Element>?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDetailViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detail_photo_property, parent, false)
        return ItemDetailViewHolder(v)
    }

    override fun onBindViewHolder(holder: ItemDetailViewHolder, position: Int) {
        var clicked = false
        val element: Element = mElement!![position]
        Glide.with(holder.mImageView.context)
            .load(element.photo)
            .centerCrop()
            .into(holder.mImageView)
        holder.mImageView.setOnClickListener { view: View? ->
            if (clicked) {
                element.isSelected = false
                clicked = !clicked
                holder.mImageView.setBackgroundResource(R.drawable.no_border)

            }
            else {
                element.isSelected = true
                holder.mImageView.setBackgroundResource(R.drawable.border)
                clicked = !clicked
            }
        }
    }

    override fun getItemCount(): Int {
        var size = 0
        if (mElement != null) {
            size = mElement.size
        }
        return size
    }

    init {
        mElement = pPhotoProperty
    }

}

data class Element(val photo: String = "", val propertyId: String? = "", var isSelected : Boolean, var elementId: String? = "") {
    constructor() : this("","",false,"")
}