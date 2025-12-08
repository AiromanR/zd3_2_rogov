package com.example.zd3_1_rogov

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuestRecycler(
    private val context: Context,
    private val list: ArrayList<Quests>
) : RecyclerView.Adapter<QuestRecycler.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val root = LayoutInflater.from(context).inflate(
            R.layout.quests_adapter,
            parent,
            false
        )
        return MyViewHolder(root)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_quests)
        val title: TextView = itemView.findViewById(R.id.title_quests)
        val descr: TextView = itemView.findViewById(R.id.descr)
        val btDetails: TextView = itemView.findViewById(R.id.bt_details)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val quest = list[position]
        holder.imageView.setImageResource(quest.image)
        holder.title.text = quest.title
        holder.descr.text = quest.text

        holder.btDetails.setOnClickListener {
        }
    }

    override fun getItemCount(): Int = list.size
}