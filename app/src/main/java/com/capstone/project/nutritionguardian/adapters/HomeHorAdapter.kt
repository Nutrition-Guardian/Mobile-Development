package com.capstone.project.nutritionguardian.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project.nutritionguardian.R
import com.capstone.project.nutritionguardian.data.Food
import com.google.android.material.imageview.ShapeableImageView

class HomeHorAdapter (private val foodList: ArrayList<Food>) : RecyclerView.Adapter<HomeHorAdapter.MyViewHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHorAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_horizontal_item,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HomeHorAdapter.MyViewHolder, position: Int) {
        val currentItem = foodList[position]
        holder.itemImage.setImageResource(currentItem.foodImage)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val itemImage: ImageView = itemView.findViewById(R.id.img_horizontal)
    }
}