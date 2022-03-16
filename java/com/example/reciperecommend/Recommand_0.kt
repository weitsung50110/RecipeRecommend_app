package com.example.reciperecommend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class Recommand_0 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommand_0)

        //使用SharedPreferences紀錄登入狀態
        val LoginSetting2 = getSharedPreferences("Have_eaten", 0)
        LoginSetting2.edit()
            .clear()
            .commit()

        //自己加
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        val db = Firebase.firestore
        //var Calories2 = LoginSetting.getString("Calories","").toString().toDouble()
        var ID = LoginSetting.getString("ID","").toString()
        var Mor_Eaten_Calories:Double = 0.0
        var Noon_Eaten_Calories:Double = 0.0
        var Dinner_Eaten_Calories:Double = 0.0
        var Mor_Count:Int = 0
        var Noon_Count:Int = 0
        var Dinner_Count:Int = 0

        db.collection(ID)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val Firbase_Date = document.getString("Date")//在55行
                    val Firbase_Time = document.getString("Time")//早餐,午餐,晚餐
                    if(Firbase_Date == getDate()){
                        if(Firbase_Time=="早餐"){
                            Mor_Eaten_Calories = Mor_Eaten_Calories + document.getDouble("熱量").toString().toDouble()
                            Mor_Count++
                        }
                        else if(Firbase_Time=="午餐"){
                            Noon_Eaten_Calories = Noon_Eaten_Calories + document.getDouble("熱量").toString().toDouble()
                            Noon_Count++
                        }
                        else if(Firbase_Time=="晚餐"){
                            Dinner_Eaten_Calories = Dinner_Eaten_Calories + document.getDouble("熱量").toString().toDouble()
                            Dinner_Count++
                        }
                        //使用SharedPreferences紀錄登入狀態
                        val LoginSetting2 = getSharedPreferences("Have_eaten", 0)
                        LoginSetting2.edit()
                            .putString("Mor_Eaten_Calories", Mor_Eaten_Calories.toString())
                            .putString("Noon_Eaten_Calories", Noon_Eaten_Calories.toString())
                            .putString("Dinner_Eaten_Calories", Dinner_Eaten_Calories.toString())
                            .putString("Mor_Count", Mor_Count.toString())
                            .putString("Noon_Count", Noon_Count.toString())
                            .putString("Dinner_Count", Dinner_Count.toString())
                            .commit()

                    }
                }
            }


        Thread.sleep(3_000)  // wait for 3 second
        val intent = Intent()
        intent.setClass(this, Recommand::class.java)
        startActivity(intent)

        //into_button0.setOnClickListener(mListener_into)
        //refresh_button0.setOnClickListener(mListener_refresh)

    }
    private val mListener_into = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this, Recommand::class.java)
        startActivity(intent)

    }
    private val mListener_refresh = View.OnClickListener {
        val LoginSetting2 = getSharedPreferences("Have_eaten", 0)
        LoginSetting2.edit()
            .clear()
            .commit()

        val LoginSetting3 = getSharedPreferences("Fixed", 0)
        LoginSetting3.edit()
            .clear()
            .commit()

    }
    fun getDate(): String{

        var calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR).toString()
        var month = ((calendar.get(Calendar.MONTH).toString().toInt())+1).toString()
        var date = calendar.get(Calendar.DAY_OF_MONTH).toString()
        if(month.toInt()<10){
            month = "0"+ month //如國值小於10,前面就補0
        }
        if(date.toInt()<10){
            date = "0" + date //如國值小於10,前面就補0
        }
        var created = year+month+date
        return created
    }

    /*private val mListener_GO = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this, Recommand::class.java)
        startActivity(intent)
    }*/
}
