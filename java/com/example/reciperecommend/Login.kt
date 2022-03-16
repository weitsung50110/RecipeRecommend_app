package com.example.reciperecommend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.*
import java.io.IOException

class Login : AppCompatActivity() {
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        button_Login_Login.setOnClickListener(mListener_Login)
        button_Login_SignUp.setOnClickListener(mListener_SignUp)

        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "no value")
    }
    private val mListener_Login = View.OnClickListener {
        //連結user_info之firebase資料庫
        db.collection("user_info")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val user_ID = document.getString("ID")
                    val user_Password = document.getString("Password")
                    val user_Calories = document.getString("Calories")
                    val user_Vege = document.getString("Vege") //自己加
                    val Recipe_type1 = document.getString("全榖雜糧類") //自己加
                    val Recipe_type2 = document.getString("豆魚蛋肉類") //自己加
                    val Recipe_type3 = document.getString("蔬菜類") //自己加
                    val Recipe_type4 = document.getString("水果類") //自己加
                    val Recipe_type5 = document.getString("乳品類") //自己加
                    val Recipe_type6 = document.getString("油脂與堅果類") //自己加
                    //比對登入的帳號密碼
                    if (editText_Login_ID.text.toString().equals(user_ID) && editText_Login_Password.text.toString().equals(user_Password))
                    {
                        //使用SharedPreferences紀錄登入狀態
                        val LoginSetting = getSharedPreferences("UserDefault", 0)
                        LoginSetting.edit()
                            .putString("ID", user_ID)
                            .putString("Password", user_Password)
                            .putString("Calories", user_Calories)
                            .putString("Vege", user_Vege)//自己加
                            .putString("Login_Recipe_type1", Recipe_type1)//自己加
                            .putString("Login_Recipe_type2", Recipe_type2)//自己加
                            .putString("Login_Recipe_type3", Recipe_type3)//自己加
                            .putString("Login_Recipe_type4", Recipe_type4)//自己加
                            .putString("Login_Recipe_type5", Recipe_type5)//自己加
                            .putString("Login_Recipe_type6", Recipe_type6)//自己加
                            .commit()

                        //登入後跳回主頁面
                        val intent = Intent()
                        intent.setClass(this@Login, Main2Activity::class.java)
                        startActivity(intent)
                        break
                    }
                    else
                    {
//                        Toast.makeText(this, "未註冊或帳號密碼錯誤", Toast.LENGTH_LONG).show()
                    }

                }
            }

    }
    private val mListener_SignUp = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@Login, SignUp::class.java)
        startActivity(intent)
    }
}
