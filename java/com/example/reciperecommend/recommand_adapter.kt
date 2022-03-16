package com.example.reciperecommend

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.food_row.view.*

class recommand_adapter (val arrayList2:ArrayList<recommand_model>, val context: Context) : RecyclerView.Adapter<recommand_adapter.ViewHolder2>(){
    class ViewHolder2(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindItems (model2:recommand_model){
            itemView.titleTv.text=model2.Name
            itemView.desTv.text=model2.Type

            itemView.VegetarianTv.text=model2.Vegetarian
            //itemView.Recipe_type1Tv.text="全榖雜糧類 : "+model2.Recipe_type1
            //itemView.Recipe_type2Tv.text="蛋豆魚肉類 : "+model2.Recipe_type2
            //itemView.Recipe_type3Tv.text="蔬菜類 : "+model2.Recipe_type3
            //itemView.Recipe_type4Tv.text="水果類 : "+model2.Recipe_type4
            //itemView.Recipe_type5Tv.text="乳品類 : "+model2.Recipe_type5
            //itemView.Recipe_type6Tv.text="油脂與堅果類 : "+model2.Recipe_type6
            //itemView.type.text="yoyo"
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recommand_adapter.ViewHolder2 {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.food_row,parent,false)
        return recommand_adapter.ViewHolder2(v)
    }

    override fun getItemCount(): Int {
        return arrayList2.size
    }

    override fun onBindViewHolder(holder: recommand_adapter.ViewHolder2, position: Int) {

        holder.bindItems(arrayList2[position])

        holder.itemView.setOnClickListener {
            Toast.makeText(context,""+holder.itemView.titleTv.text+ " Yes~~", Toast.LENGTH_SHORT).show()

            val model3 = arrayList2.get(position)
            var gTitle:String = model3.Name
            var gType:String = model3.Type

            val intent = Intent(context,recommand_detail::class.java)

            intent.putExtra("rTitle",gTitle)
            intent.putExtra("rType",gType)

            intent.putExtra("rVegetarian",model3.Vegetarian)
            intent.putExtra("rID",model3.ID)
            intent.putExtra("rTotal",model3.Total)
            intent.putExtra("rCarbohydrates",model3.Carbohydrates)
            intent.putExtra("rProtein",model3.Protein)
            intent.putExtra("rFat",model3.Fat)

            intent.putExtra("rRecipe_type1",model3.Recipe_type1)
            intent.putExtra("rRecipe_type2",model3.Recipe_type2)
            intent.putExtra("rRecipe_type3",model3.Recipe_type3)
            intent.putExtra("rRecipe_type4",model3.Recipe_type4)
            intent.putExtra("rRecipe_type5",model3.Recipe_type5)
            intent.putExtra("rRecipe_type6",model3.Recipe_type6)

            intent.putExtra("rRecipe_method",model3.method)
            intent.putExtra("rRecipe_date",model3.Date)
            intent.putExtra("rRecipe_Meal_time",model3.Meal_time)

            context.startActivity(intent)
        }
    }
}