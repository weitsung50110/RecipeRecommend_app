package com.example.reciperecommend

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.no_row.view.*

class no_adapter (val arrayList2:ArrayList<no_model>, val context: Context) : RecyclerView.Adapter<no_adapter.ViewHolder2>(){
    class ViewHolder2(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindItems (model2:no_model){
            itemView.No_titleTv.text=model2.Title
            itemView.No_desTv.text=model2.Des

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): no_adapter.ViewHolder2 {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.no_row,parent,false)
        return no_adapter.ViewHolder2(v)
    }

    override fun getItemCount(): Int {
        return arrayList2.size
    }

    override fun onBindViewHolder(holder: no_adapter.ViewHolder2, position: Int) {

        holder.bindItems(arrayList2[position])

        holder.itemView.setOnClickListener {
            Toast.makeText(context,"熱量已經超標~", Toast.LENGTH_SHORT).show()


        }
    }
}