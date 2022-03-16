package com.example.reciperecommend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_recommand_detail.*

class recommand_detail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommand_detail)


        var intent2 = intent
        var aTitle = intent2.getStringExtra("rTitle")
        var aType = intent2.getStringExtra("rType")

        var aVegetarian = intent2.getStringExtra("rVegetarian")
        var aID = intent2.getStringExtra("rID")
        var aTotal = intent2.getStringExtra("rTotal")
        var aCarbohydrates = intent2.getStringExtra("rCarbohydrates")
        var aProtein = intent2.getStringExtra("rProtein")
        var aFat = intent2.getStringExtra("rFat")

        var aRecipe_type1 = intent2.getStringExtra("rRecipe_type1")
        var aRecipe_type2 = intent2.getStringExtra("rRecipe_type2")
        var aRecipe_type3 = intent2.getStringExtra("rRecipe_type3")
        var aRecipe_type4 = intent2.getStringExtra("rRecipe_type4")
        var aRecipe_type5 = intent2.getStringExtra("rRecipe_type5")
        var aRecipe_type6 = intent2.getStringExtra("rRecipe_type6")

        var aRecipe_method = intent2.getStringExtra("rRecipe_method")
        var aRecipe_date = intent2.getStringExtra("rRecipe_date")
        var aRecipe_Meal_time = intent2.getStringExtra("rRecipe_Meal_time")


        rec_title.text="菜餚名稱 : "+aTitle
        rec_date.text="日期 : "+aRecipe_date
        rec_Meal_time.text="吃飯時段 : "+aRecipe_Meal_time
        rec_method.text="作法 : \n"+aRecipe_method.replace("\\n", "\n")
        rec_des.text="食品分類 : "+aType

        rec_des2.text = "ID : "+aID
        rec_des3.text = "葷素 : "+aVegetarian
        rec_des4.text = "熱量 : "+aTotal
        rec_des5.text = "碳水化合物 : "+aCarbohydrates
        rec_des6.text = "蛋白質 : "+aProtein
        rec_des7.text = "脂肪 : "+aFat

        rec_des8.text = "全榖雜糧類 : "+aRecipe_type1
        rec_des9.text = "豆魚蛋肉類 : "+aRecipe_type2
        rec_des10.text = "蔬菜類 : "+aRecipe_type3
        rec_des11.text = "水果類 : "+aRecipe_type4
        rec_des12.text = "乳品類 : "+aRecipe_type5
        rec_des13.text = "油脂與堅果類 : "+aRecipe_type6


        rec_detail_add.setOnClickListener(mListener_add)
        rec_detail_back.setOnClickListener(mListener_back)


    }
    private val mListener_add = View.OnClickListener {
        var intent2 = intent
        var aTitle = intent2.getStringExtra("rTitle")
        var aType = intent2.getStringExtra("rType")

        var aVegetarian = intent2.getStringExtra("rVegetarian")
        var aID = intent2.getStringExtra("rID")
        var aTotal = intent2.getStringExtra("rTotal")
        var aCarbohydrates = intent2.getStringExtra("rCarbohydrates")
        var aProtein = intent2.getStringExtra("rProtein")
        var aFat = intent2.getStringExtra("rFat")

        var aRecipe_type1 = intent2.getStringExtra("rRecipe_type1")
        var aRecipe_type2 = intent2.getStringExtra("rRecipe_type2")
        var aRecipe_type3 = intent2.getStringExtra("rRecipe_type3")
        var aRecipe_type4 = intent2.getStringExtra("rRecipe_type4")
        var aRecipe_type5 = intent2.getStringExtra("rRecipe_type5")
        var aRecipe_type6 = intent2.getStringExtra("rRecipe_type6")

        var aRecipe_method = intent2.getStringExtra("rRecipe_method")
        var aRecipe_date = intent2.getStringExtra("rRecipe_date")
        var aRecipe_Meal_time = intent2.getStringExtra("rRecipe_Meal_time")

        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var ID = LoginSetting.getString("ID","").toString()

        val db = Firebase.firestore

        // Create a new user with a first and last name
        val notes_book = hashMapOf(
            "Date" to aRecipe_date,
            "Name" to aTitle,
            "Time" to aRecipe_Meal_time,
            "Type" to "Recipe",
            "作法" to aRecipe_method,
            "全榖雜糧類" to aRecipe_type1,
            "豆魚蛋肉類" to aRecipe_type2.toString().toDouble(),
            "蔬菜類" to aRecipe_type3.toString().toDouble(),
            "水果類" to aRecipe_type4.toString().toDouble(),
            "乳品類" to aRecipe_type5.toString().toDouble(),
            "油脂與堅果類" to aRecipe_type6.toString().toDouble(),
            "熱量" to aTotal.toString().toDouble(),
            "碳水化合物" to aCarbohydrates.toString().toDouble(),
            "蛋白質" to aProtein.toString().toDouble(),
            "脂質" to aFat.toString().toDouble(),
            "葷素" to aVegetarian
        )

        db.collection(ID).document(aRecipe_date+aRecipe_Meal_time+aTitle)
            .set(notes_book)  //資料庫 集合名字 >>  companies    ,   並且把欄位 "name" , "address" , "number" 丟進去資料庫 集合裡面~~
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "新增成功 !!", Toast.LENGTH_SHORT).show()
            }
    }

    private val mListener_back = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this, Recommand
        ::class.java)
        startActivity(intent)

    }
}