package com.mawar.glowskin.AdapterAdmin

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mawar.glowskin.ActivityAdmin.ActivityDetailAdmin
import com.mawar.glowskin.Model.ItemsModel
import com.mawar.glowskin.databinding.ViewholderRecommendBinding

class ListItemsAdminAdapter (private val items: List<ItemsModel>) : RecyclerView.Adapter<ListItemsAdminAdapter.ViewHolder>() {

    class ViewHolder (val binding: ViewholderRecommendBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderRecommendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.titleTxt.text = item.title
        holder.binding.priceTxt.text = String.format("$%.2f", item.price)
        holder.binding.ratingTxt.text = item.rating.toString()

        Glide.with(holder.itemView.context)
            .load(item.picUrl[0])
            .apply(RequestOptions.centerCropTransform())
            .into(holder.binding.pic)

        holder.itemView.setOnClickListener{
            val intent= Intent(holder.itemView.context, ActivityDetailAdmin::class.java)
            intent.putExtra("object", items[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

}