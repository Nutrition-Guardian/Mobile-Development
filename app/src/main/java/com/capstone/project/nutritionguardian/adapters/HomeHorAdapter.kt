package com.capstone.project.nutritionguardian.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project.nutritionguardian.R
import com.capstone.project.nutritionguardian.data.Food
import com.capstone.project.nutritionguardian.data.Logbook

class HomeHorAdapter(private var items: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val FOOD_TYPE = 0
        const val LOGBOOK_TYPE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FOOD_TYPE -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.home_horizontal_item, parent, false)
                FoodViewHolder(itemView)
            }
            LOGBOOK_TYPE -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.logbook_item, parent, false)
                LogbookViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            FOOD_TYPE -> {
                val foodHolder = holder as FoodViewHolder
                val foodItem = items[position] as Food
                foodHolder.itemImage.setImageResource(foodItem.foodImage)
            }
            LOGBOOK_TYPE -> {
                val logbookHolder = holder as LogbookViewHolder
                val logbookItem = items[position] as Logbook
                logbookHolder.logText.text = logbookItem.logbookText
                logbookHolder.logDate.text = logbookItem.currentDate
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is Food) {
            FOOD_TYPE
        } else if (items[position] is Logbook) {
            LOGBOOK_TYPE
        } else {
            throw IllegalArgumentException("Invalid item type")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newItems: List<Any>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.img_horizontal)
    }

    inner class LogbookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logText: TextView = itemView.findViewById(R.id.logDesc)
        val logDate: TextView = itemView.findViewById(R.id.logDate)
    }
}
