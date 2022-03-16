package com.example.reciperecommend

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_history__detail.*
import kotlinx.android.synthetic.main.activity_record__recipe.*
import java.util.ArrayList

class History_Detail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history__detail)

        val getTitle = intent.getStringExtra("getTitle")
        getDetail(getTitle)

        History_Detail_Delete.setOnClickListener(mListener_HistoryDetail_Delete)
        History_Detail_OK.setOnClickListener(mListener_HistoryDetail_OK)
    }

    private val mListener_HistoryDetail_Delete = View.OnClickListener {
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "no value").toString()
        val getTitle = intent.getStringExtra("getTitle")
        db.collection(GetID)
            .document(getTitle)
            .delete()
        val intent = Intent()
        intent.setClass(this@History_Detail, History::class.java)
        startActivity(intent)
    }
    private val mListener_HistoryDetail_OK = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@History_Detail, History::class.java)
        startActivity(intent)
    }
    fun getDetail(getTitle :String){
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "no value").toString()

        db.collection(GetID)
            .document(getTitle)
            .get()
            .addOnSuccessListener{ result ->
                History_Detail_Date.text = result.get("Date").toString()
                History_Detail_Time.text = result.get("Time").toString()
                History_Detail_Name.text = result.get("Name").toString()

                val HistoryDetail_Name = ArrayList<String>()
                val HistoryDetail_Quan = ArrayList<String>()

                HistoryDetail_Name.add("熱量")
                HistoryDetail_Quan.add(result.get("熱量").toString())

                HistoryDetail_Name.add("全榖雜糧類")
                HistoryDetail_Quan.add(result.get("全榖雜糧類").toString())

                HistoryDetail_Name.add("豆魚蛋肉類")
                HistoryDetail_Quan.add(result.get("豆魚蛋肉類").toString())

                HistoryDetail_Name.add("蔬菜類")
                HistoryDetail_Quan.add(result.get("蔬菜類").toString())

                HistoryDetail_Name.add("水果類")
                HistoryDetail_Quan.add(result.get("水果類").toString())

                HistoryDetail_Name.add("乳品類")
                HistoryDetail_Quan.add(result.get("乳品類").toString())

                HistoryDetail_Name.add("油脂與堅果類")
                HistoryDetail_Quan.add(result.get("油脂與堅果類").toString())

                //將顯示的資料給recyclerview
                val layoutManager = LinearLayoutManager(this)
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                History_Detail_Recycler.layoutManager = layoutManager
                History_Detail_Recycler.adapter = DataAdapter_HistoryDetail(HistoryDetail_Name,HistoryDetail_Quan,this)
            }




    }
}

class DataAdapter_HistoryDetail(private val iName: List<String>,private val iQuan: List<String>, val context: Context) : RecyclerView.Adapter<ViewHolder_HistoryDetail>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_HistoryDetail {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recipe_inger, null, false)
        return ViewHolder_HistoryDetail(v)
    }
    override fun onBindViewHolder(holder: ViewHolder_HistoryDetail, position: Int) {
        holder.dataView_Name.text = iName[position]
        holder.dataView_Quan.text = iQuan[position]
        holder.itemView.setOnClickListener {
        }
        holder.itemView.setOnLongClickListener {
//            Toast.makeText(it.context, "Item $position is long clicked.", Toast.LENGTH_SHORT).show()
            true
        }
    }
    override fun getItemCount(): Int {
        return iName.size
    }
}
class ViewHolder_HistoryDetail(v: View) : RecyclerView.ViewHolder(v) {
    val dataView_Name: TextView = v.findViewById(R.id.recipe_inger_name)
    val dataView_Quan: TextView = v.findViewById(R.id.recipe_inger_quantity)
}
private fun initRecyclerView() {
}