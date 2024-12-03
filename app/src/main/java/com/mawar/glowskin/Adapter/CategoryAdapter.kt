package com.mawar.glowskin.Adapter

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mawar.glowskin.Activity.ListItemsActivity
import com.mawar.glowskin.Model.CategoryModel
import com.mawar.glowskin.R
import com.mawar.glowskin.databinding.ViewholderCategoryBinding

class CategoryAdapter(private val items: List<CategoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private var selectedPosition = -1
    private var lastClickedPosition = -1

    inner class ViewHolder(val binding: ViewholderCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.titleText.text = item.title

        Glide.with(holder.itemView.context)
            .load(item.picUrl)
            .into(holder.binding.pic)

        if (selectedPosition == position) {
            holder.binding.pic.setBackgroundResource(0)
            holder.binding.mainLayout.setBackgroundResource(R.drawable.red_bg)
            ImageViewCompat.setImageTintList(
                holder.binding.pic,
                ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.white))
            )
            holder.binding.titleText.visibility = View.VISIBLE
            holder.binding.titleText.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        } else {
            holder.binding.pic.setBackgroundResource(R.drawable.grey_bg)
            holder.binding.mainLayout.setBackgroundResource(0)
            ImageViewCompat.setImageTintList(
                holder.binding.pic,
                ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.rec))
            )
            holder.binding.titleText.visibility = View.GONE
            holder.binding.titleText.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.rec))
        }

        holder.binding.root.setOnClickListener {
            val pos = position
            if (pos != RecyclerView.NO_POSITION) {

                if (lastClickedPosition == pos) {
                    selectedPosition = -1
                    holder.binding.titleText.visibility = View.GONE
                } else {
                    lastClickedPosition = selectedPosition
                    selectedPosition = pos
                }

                notifyItemChanged(lastClickedPosition)
                notifyItemChanged(selectedPosition)

                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(holder.itemView.context, ListItemsActivity::class.java).apply {
                        putExtra("id", item.id.toString())
                        putExtra("title", item.title)
                    }
                    ContextCompat.startActivity(holder.itemView.context, intent, null)
                }, 500)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
