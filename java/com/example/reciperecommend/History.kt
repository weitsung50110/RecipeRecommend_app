package com.example.reciperecommend

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_record.*
import kotlinx.android.synthetic.main.activity_record__recipe.*
import java.text.SimpleDateFormat
import java.util.*

class History : AppCompatActivity() {
    var flag:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        
        History_Date.text = "日期："+getDate()
        showHistory(getDate())
        History_Date.setOnClickListener(mListener_History_Date)
        History_Cancel.setOnClickListener(mListener_History_Cancel)

        History_Search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(s!=null){
                    val LoginSetting = getSharedPreferences("UserDefault", 0)
                    var GetID = LoginSetting.getString("ID", "no value").toString()

                    val History_Name = ArrayList<String>()
                    val History_Date = ArrayList<String>()
                    val History_Time = ArrayList<String>()
                    val History_Calories = ArrayList<String>()
                    db.collection(GetID)
                        .get()
                        .addOnSuccessListener{ result ->
                            for (document in result)
                            {
//                            textView.append(document.getString("Name").toString())
                                if (document.getString("Name").toString().indexOf(History_Search.text.toString(),0)>=0)
                                {
//                                textView.append(History_Search.text.toString()+document.getString("Name"))
                                    History_Name.add(document.getString("Name").toString())
                                    History_Date.add(document.getString("Date").toString())
                                    History_Time.add(document.getString("Time").toString())
                                    History_Calories.add(document.get("熱量").toString())
                                    flag += 1
                                }
                            }
                            val layoutManager = LinearLayoutManager(this@History)
                            layoutManager.orientation = LinearLayoutManager.VERTICAL
                            History_Recycler.layoutManager = layoutManager
                            History_Recycler.adapter = DataAdapter_History(History_Name,History_Date,History_Time,History_Calories,this@History)
                        }
                }
                else if (count == 0){
                    showHistory(getDate())
                }

            }



        }) //關鍵字搜尋

        imageButton_RecomHistory.setOnClickListener(mListener_Main3)
        imageButton_HomeHistory.setOnClickListener(mListener_History_goHome)
        imageButton_RecordHistory.setOnClickListener(mListener_History_goRecord)
        imageButton_SearchHistory.setOnClickListener(mListener_History_goSearch)

    }
    private val mListener_Main3 = View.OnClickListener {
        val dialog = ProgressDialog.show(this@History,"讀取中", "請等待...", true)
        Thread(Runnable {
            try {
                Thread.sleep(10000)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                dialog!!.dismiss()
            }
        }).start()

        val intent = Intent()
        intent.setClass(this, Recommand_0::class.java)
        startActivity(intent)

    }
    private val mListener_History_goHome = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@History, Main2Activity::class.java)
        startActivity(intent)
    }
    private val mListener_History_goRecord = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@History, Record::class.java)
        startActivity(intent)
    }
    private val mListener_History_goSearch = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@History, SearchRecipe::class.java)
        startActivity(intent)
    }
    private val mListener_History_Cancel = View.OnClickListener {
        History_Search.setText(null) //editText內容清空
        showHistory(getDate())
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = window.peekDecorView()
        if (null != v) {
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }
    private val mListener_History_Date = View.OnClickListener {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, { _, year, month, day ->
            c.set(Calendar.YEAR, year)
            c.set(Calendar.MONTH, month)
            c.set(Calendar.DAY_OF_MONTH, day)
            val Date = SimpleDateFormat("yyyyMMdd", Locale.TAIWAN)
            History_Date.text = "日期："+Date.format(c.time)
            showHistory(Date.format(c.time))
        }, year, month, day).show()

    }

    fun showHistory(Date:String)
    {
        flag = 0
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "no value").toString()

        val History_Name = ArrayList<String>()
        val History_Date = ArrayList<String>()
        val History_Time = ArrayList<String>()
        val History_Calories = ArrayList<String>()
        db.collection(GetID)
            .get()
            .addOnSuccessListener{ result ->
                for (document in result)
                {
                    if (document.getString("Date").equals(Date))
                    {
//                        textView.append(document.getString("Name"))
                        History_Name.add(document.getString("Name").toString())
                        History_Date.add(document.getString("Date").toString())
                        History_Time.add(document.getString("Time").toString())
                        History_Calories.add(document.get("熱量").toString())
                        flag += 1
                    }
                }
                val layoutManager = LinearLayoutManager(this)
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                History_Recycler.layoutManager = layoutManager
                History_Recycler.adapter = DataAdapter_History(History_Name,History_Date,History_Time,History_Calories ,this)
            }
    }
}

class DataAdapter_History(private val hName: List<String>,private val hDate: List<String>,private val hTime: List<String>,private val hCal: List<String>, val context: Context) : RecyclerView.Adapter<ViewHolder_History>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_History {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.history_item, null, false)
        return ViewHolder_History(v)
    }
    override fun onBindViewHolder(holder: ViewHolder_History, position: Int) {
        holder.dataView_Name.text = hName[position]
        holder.dataView_Quan.text = hDate[position] +" "+ hTime[position]
        holder.dataView_Cal.text = "熱量："+ hCal[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(context,History_Detail::class.java)
            intent.putExtra("getTitle", hDate[position]+hTime[position]+hName[position])
            context.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener {
//            Toast.makeText(it.context, "Item $position is long clicked.", Toast.LENGTH_SHORT).show()
            true
        }
    }
    override fun getItemCount(): Int {
        return hName.size
    }
}
class ViewHolder_History(v: View) : RecyclerView.ViewHolder(v) {
    val dataView_Name: TextView = v.findViewById(R.id.HistoryItem_Name)
    val dataView_Quan: TextView = v.findViewById(R.id.HistoryItem_Date)
    val dataView_Cal: TextView = v.findViewById(R.id.HistoryItem_Detail)
}

