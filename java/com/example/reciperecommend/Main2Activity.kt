package com.example.reciperecommend

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main2.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Main2Activity : AppCompatActivity() {
    var flag:Int = 0
    var Calories:Float = 0f
    var Carbohydrates: Float=0.00f //碳水化合物
    var protein: Float=0.00f //蛋白質
    var fat: Float=0.00f //脂質

    var Type1: Float=0.00f
    var Type2: Float=0.00f
    var Type3: Float=0.00f
    var Type4: Float=0.00f
    var Type5: Float=0.00f
    var Type6: Float=0.00f
    //1.全榖根莖 2.豆魚蛋肉 3.蔬菜類 4.水果類 5.乳品 6.油脂堅果
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        Main2_Date.text = "日期：" + getDate()
        Main2_LineDateF.text = "選擇起始日期"
        Main2_LineDateL.text = getDate()
        Draw(getDate())

        //圓餅圖日期
        Main2_Date.setOnClickListener(mListener_Main2Date)
        imageButton_RecomMain2.setOnClickListener(mListener_Main3)
        imageButton_HistoryMain2.setOnClickListener(mListener_GoHistory)
        imageButton_RecordMain2.setOnClickListener(mListener_GoRecord)
        imageButton_SearchMain2.setOnClickListener(mListener_GoSearch)
        Main2_GoLine.setOnClickListener(mListener_GoLine)
        Main2_GoPie.setOnClickListener(mListener_GoPie)
        Main2_GoUser.setOnClickListener(mListener_GoUser)

        //折線圖選日期
        Main2_LineDateF.setOnClickListener(mListener_Main2_LineDateF)
        Main2_LineDateL.setOnClickListener(mListener_Main2_LineDateL)

        //六大類折線圖
        Main2_LineCalories.setOnClickListener(mListener_Main2_LineCalories)
        Main2_LineType1.setOnClickListener(mListener_Main2_LineType1)
        Main2_LineType2.setOnClickListener(mListener_Main2_LineType2)
        Main2_LineType3.setOnClickListener(mListener_Main2_LineType3)
        Main2_LineType4.setOnClickListener(mListener_Main2_LineType4)
        Main2_LineType5.setOnClickListener(mListener_Main2_LineType5)
        Main2_LineType6.setOnClickListener(mListener_Main2_LineType6)
    }

    private val mListener_Main2Date = View.OnClickListener {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, { _, year, month, day ->
            c.set(Calendar.YEAR, year)
            c.set(Calendar.MONTH, month)
            c.set(Calendar.DAY_OF_MONTH, day)
            val Date = SimpleDateFormat("yyyyMMdd", Locale.TAIWAN)
            Main2_Date.text = "日期："+Date.format(c.time).toString()
            Draw(Date.format(c.time).toString())
        }, year, month, day).show()

    }

    private val mListener_GoPie = View.OnClickListener {
        Main2_PieChartScroll.setVisibility(View.VISIBLE)
        Main2_LineChartScroll.setVisibility(View.GONE)
    }
    fun Draw(date:String){
        flag = 0
        Calories = 0f
        Type1 = 0f
        Type2 = 0f
        Type3 = 0f
        Type4 = 0f
        Type5 = 0f
        Type6 = 0f
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "no value").toString()
        var GetCalories = LoginSetting.getString("Calories", "no value").toString().toFloat()
        val db = Firebase.firestore
        db.collection(GetID)
            .get()
            .addOnSuccessListener{ result ->
                for (document in result)
                {
                    if (document.getString("Date").equals(date))
                    {
                        Calories += document.get("熱量").toString().toFloat()
                        Type1 += document.get("全榖雜糧類").toString().toFloat()
                        Type2 += document.get("豆魚蛋肉類").toString().toFloat()
                        Type3 += document.get("蔬菜類").toString().toFloat()
                        Type4 += document.get("水果類").toString().toFloat()
                        Type5 += document.get("乳品類").toString().toFloat()
                        Type6 += document.get("油脂與堅果類").toString().toFloat()
                        drawCal(GetCalories, Calories)
                        DrawSix(Type1,Type2,Type3,Type4,Type5,Type6)
                        flag += 1
                    }
                }
            }
        if (flag == 0)
        {
            drawCal(GetCalories, 0f)
            DrawSix(0f,0f,0f,0f,0f,0f)
        }
    }
    fun drawCal(full:Float, cal:Float){
        val piechart = mPieChart_Calories
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(cal,"已攝入"))
        val colors = ArrayList<Int>()
        if (full >= cal)
        {
            entries.add(PieEntry(full-cal,"未攝入"))
            colors.add(resources.getColor(R.color.Calories))
        }
        else if (full < cal)
        {
            entries.add(PieEntry(0f,"未攝入"))
            colors.add(resources.getColor(R.color.Over))
        }
        colors.add(resources.getColor(R.color.chartBG))

        val dataSet = PieDataSet(entries,"")
        dataSet.setColors(colors)

        val pieData = PieData(dataSet)
        pieData.setDrawValues(true)

        pieData.setValueTextSize(15f);
        piechart.setData(pieData)
        piechart.setCenterText("標準熱量："+full)
        piechart.setCenterTextSize(20f)
        piechart.setRotationEnabled(false)

        val l: Legend = piechart.getLegend()
        l.setEnabled(false);
        piechart.invalidate()
    }

    fun DrawSix(Type1:Float,Type2:Float,Type3:Float,Type4:Float,Type5:Float,Type6:Float){
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "no value").toString()
        db.collection("user_info")
            .document(GetID)
            .get()
            .addOnSuccessListener { result ->
                //全榖雜糧類
                val piechart_1 = mPieChart_1
                val entries_1 = ArrayList<PieEntry>()
                val colors_1 = ArrayList<Int>()
                entries_1.add(PieEntry(Type1,"已攝入"))
                if (result.get("全榖雜糧類").toString().toFloat() >= Type1)
                {
                    entries_1.add(PieEntry(result.get("全榖雜糧類").toString().toFloat()-Type1,"未攝入"))
                    colors_1.add(resources.getColor(R.color.Type1))
                }
                else if (result.get("全榖雜糧類").toString().toFloat() < Type1)
                {
                    entries_1.add(PieEntry(0f,"未攝入"))
                    colors_1.add(resources.getColor(R.color.Over))
                }
                colors_1.add(resources.getColor(R.color.chartBG))
                val dataSet_1 = PieDataSet(entries_1,"")
                dataSet_1.setColors(colors_1)
                val pieData_1 = PieData(dataSet_1)
                pieData_1.setDrawValues(true)
                pieData_1.setValueTextSize(10f)
                piechart_1.setData(pieData_1)
                piechart_1.setCenterText("全榖雜糧")
                piechart_1.setCenterTextSize(15f)
                piechart_1.setHoleRadius(70f)
                piechart_1.setRotationEnabled(false)
                val l_1: Legend = piechart_1.getLegend()
                l_1.setEnabled(false);
                piechart_1.invalidate()

                //豆魚蛋肉類
                val piechart_2 = mPieChart_2
                val entries_2 = ArrayList<PieEntry>()
                val colors_2 = ArrayList<Int>()
                entries_2.add(PieEntry(Type2,"已攝入"))
                if (result.get("豆魚蛋肉類").toString().toFloat() >= Type2)
                {
                    entries_2.add(PieEntry(result.get("豆魚蛋肉類").toString().toFloat()-Type2,"未攝入"))
                    colors_2.add(resources.getColor(R.color.Type2))
                }
                else if (result.get("豆魚蛋肉類").toString().toFloat() < Type2)
                {
                    entries_2.add(PieEntry(0f,"未攝入"))
                    colors_2.add(resources.getColor(R.color.Over))
                }
                colors_2.add(resources.getColor(R.color.chartBG))
                val dataSet_2 = PieDataSet(entries_2,"")
                dataSet_2.setColors(colors_2)
                val pieData_2 = PieData(dataSet_2)
                pieData_2.setDrawValues(true)
                pieData_2.setValueTextSize(10f)
                piechart_2.setData(pieData_2)
                piechart_2.setCenterText("豆魚蛋肉")
                piechart_2.setCenterTextSize(15f)
                piechart_2.setHoleRadius(70f)
                piechart_2.setRotationEnabled(false)
                val l_2: Legend = piechart_2.getLegend()
                l_2.setEnabled(false)
                piechart_2.invalidate()


                //蔬菜類
                val piechart_3 = mPieChart_3
                val entries_3 = ArrayList<PieEntry>()
                val colors_3 = ArrayList<Int>()
                entries_3.add(PieEntry(Type3,"已攝入"))
                if (result.get("蔬菜類").toString().toFloat() >= Type3)
                {
                    entries_3.add(PieEntry(result.get("蔬菜類").toString().toFloat()-Type3,"未攝入"))
                    colors_3.add(resources.getColor(R.color.Type3))
                }
                else if (result.get("蔬菜類").toString().toFloat() < Type3)
                {
                    entries_3.add(PieEntry(0f,"未攝入"))
                    colors_3.add(resources.getColor(R.color.Over))
                }
                colors_3.add(resources.getColor(R.color.chartBG))
                val dataSet_3 = PieDataSet(entries_3,"")
                dataSet_3.setColors(colors_3)
                val pieData_3 = PieData(dataSet_3)
                pieData_3.setDrawValues(true)
                pieData_3.setValueTextSize(10f)
                piechart_3.setData(pieData_3)
                piechart_3.setCenterText("蔬菜")
                piechart_3.setCenterTextSize(15f)
                piechart_3.setHoleRadius(70f)
                piechart_3.setRotationEnabled(false)
                val l_3: Legend = piechart_3.getLegend()
                l_3.setEnabled(false)
                piechart_3.invalidate()

                //水果類
                val piechart_4 = mPieChart_4
                val entries_4 = ArrayList<PieEntry>()
                val colors_4 = ArrayList<Int>()
                entries_4.add(PieEntry(Type4,"已攝入"))
                if (result.get("水果類").toString().toFloat() >= Type4)
                {
                    entries_4.add(PieEntry(result.get("水果類").toString().toFloat()-Type4,"未攝入"))
                    colors_4.add(resources.getColor(R.color.Type4))
                }
                else if (result.get("水果類").toString().toFloat() < Type4)
                {
                    entries_4.add(PieEntry(0f,"未攝入"))
                    colors_4.add(resources.getColor(R.color.Over))
                }
                colors_4.add(resources.getColor(R.color.chartBG))
                val dataSet_4 = PieDataSet(entries_4,"")
                dataSet_4.setColors(colors_4)
                val pieData_4 = PieData(dataSet_4)
                pieData_4.setDrawValues(true)
                pieData_4.setValueTextSize(10f)
                piechart_4.setData(pieData_4)
                piechart_4.setCenterText("水果")
                piechart_4.setCenterTextSize(15f)
                piechart_4.setHoleRadius(70f)
                piechart_4.setRotationEnabled(false)
                val l_4: Legend = piechart_4.getLegend()
                l_4.setEnabled(false)
                piechart_4.invalidate()


                //乳品類
                val piechart_5 = mPieChart_5
                val entries_5 = ArrayList<PieEntry>()
                val colors_5 = ArrayList<Int>()
                entries_5.add(PieEntry(Type5,"已攝入"))
                if (result.get("乳品類").toString().toFloat() >= Type5)
                {
                    entries_5.add(PieEntry(result.get("乳品類").toString().toFloat()-Type5,"未攝入"))
                    colors_5.add(resources.getColor(R.color.Type5))
                }
                else if (result.get("乳品類").toString().toFloat() < Type5)
                {
                    entries_5.add(PieEntry(0f,"未攝入"))
                    colors_5.add(resources.getColor(R.color.Over))
                }
                colors_5.add(resources.getColor(R.color.chartBG))
                val dataSet_5 = PieDataSet(entries_5,"")
                dataSet_5.setColors(colors_5)
                val pieData_5 = PieData(dataSet_5)
                pieData_5.setDrawValues(true)
                pieData_5.setValueTextSize(10f)
                piechart_5.setData(pieData_5)
                piechart_5.setCenterText("乳品")
                piechart_5.setCenterTextSize(15f)
                piechart_5.setHoleRadius(70f)
                piechart_5.setRotationEnabled(false)
                val l_5: Legend = piechart_5.getLegend()
                l_5.setEnabled(false)
                piechart_5.invalidate()

                //油脂與堅果類
                val piechart_6 = mPieChart_6
                val entries_6 = ArrayList<PieEntry>()
                val colors_6 = ArrayList<Int>()
                entries_6.add(PieEntry(Type6,"已攝入"))
                if (result.get("油脂與堅果類").toString().toFloat() >= Type6)
                {
                    entries_6.add(PieEntry(result.get("油脂與堅果類").toString().toFloat()-Type6,"未攝入"))
                    colors_6.add(resources.getColor(R.color.Type6))
                }
                else if (result.get("油脂與堅果類").toString().toFloat() < Type6)
                {
                    entries_6.add(PieEntry(0f,"未攝入"))
                    colors_6.add(resources.getColor(R.color.Over))
                }
                colors_6.add(resources.getColor(R.color.chartBG))
                val dataSet_6 = PieDataSet(entries_6,"")
                dataSet_6.setColors(colors_6)
                val pieData_6 = PieData(dataSet_6)
                pieData_6.setDrawValues(true)
                pieData_6.setValueTextSize(10f)
                piechart_6.setData(pieData_6)
                piechart_6.setCenterText("油脂與堅果")
                piechart_6.setCenterTextSize(15f)
                piechart_6.setHoleRadius(70f)
                piechart_6.setRotationEnabled(false)
                val l_6: Legend = piechart_6.getLegend()
                l_6.setEnabled(false)
                piechart_6.invalidate()
            }
    }
    private val mListener_Main3 = View.OnClickListener {
        val dialog = ProgressDialog.show(this@Main2Activity,"讀取中", "請等待...", true)
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
    private val mListener_GoHistory = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@Main2Activity, History::class.java)
        startActivity(intent)
    }
    private val mListener_GoRecord = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@Main2Activity, Record::class.java)
        startActivity(intent)
    }
    private val mListener_GoSearch = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@Main2Activity, SearchRecipe::class.java)
        startActivity(intent)
    }
    private val mListener_GoUser = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@Main2Activity, user::class.java)
        startActivity(intent)
    }
    private val mListener_Main2_LineDateF = View.OnClickListener {
        val cF = Calendar.getInstance()
        val cL = Calendar.getInstance()
        val year = cF.get(Calendar.YEAR)
        val month = cF.get(Calendar.MONTH)
        val day = cF.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, { _, year, month, day ->
            cF.set(Calendar.YEAR, year)
            cF.set(Calendar.MONTH, month)
            cF.set(Calendar.DAY_OF_MONTH, day)
            val Date = SimpleDateFormat("yyyyMMdd", Locale.TAIWAN)


            val yearL:Int = Main2_LineDateL.text.toString().toInt() / 10000
            val monthL:Int = (Main2_LineDateL.text.toString().toInt() - (yearL*10000))/100
            val dayL:Int = (Main2_LineDateL.text.toString().toInt() - (yearL*10000) - (monthL*100))
            cL.set(yearL,monthL-1,dayL)

            if (month < monthL-2 && year == yearL)
            {
                Toast.makeText(this, "折線圖不提供顯示超過兩個月之內容", Toast.LENGTH_SHORT).show()
            }
            else if (month > monthL-1&& year == yearL)
            {
                Toast.makeText(this, "起始日期不可大於結束日期", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Main2_LineDateF.text = Date.format(cF.time)
                getLineDate(year,month,day,yearL,monthL-1,dayL,"熱量")
            }

        }, year, month, day).show()

    }

    private val mListener_Main2_LineDateL = View.OnClickListener {
        if (Main2_LineDateF.text.toString() == "選擇起始日期")
        {
            Toast.makeText(this, "請先選擇起始日期", Toast.LENGTH_SHORT).show()
        }
        else
        {
            val cF = Calendar.getInstance()
            val cL = Calendar.getInstance()
            val year = cL.get(Calendar.YEAR)
            val month = cL.get(Calendar.MONTH)
            val day = cL.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, { _, year, month, day ->
                cL.set(Calendar.YEAR, year)
                cL.set(Calendar.MONTH, month)
                cL.set(Calendar.DAY_OF_MONTH, day)
                val Date = SimpleDateFormat("yyyyMMdd", Locale.TAIWAN)


                val yearF:Int = Main2_LineDateF.text.toString().toInt() / 10000
                val monthF:Int = (Main2_LineDateF.text.toString().toInt() - (yearF*10000))/100
                val dayF:Int = (Main2_LineDateF.text.toString().toInt() - (yearF*10000) - (monthF*100))

                cF.set(yearF,monthF-1,dayF)


                if (monthF < month && yearF == year)
                {
                    Toast.makeText(this, "起始日期不可大於結束日期", Toast.LENGTH_SHORT).show()
                }
                else if (monthF-1 > month && yearF == year)
                {
                    Toast.makeText(this, "起始日期不可大於結束日期", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Main2_LineDateL.text = Date.format(cL.time)
                    getLineDate(yearF,monthF-1,dayF,year,month,day,"熱量")
                }


            }, year, month, day).show()
        }


    }
    private val mListener_GoLine = View.OnClickListener {
        Main2_PieChartScroll.setVisibility(View.GONE)
        Main2_LineChartScroll.setVisibility(View.VISIBLE)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        getLineDate(year,month,1,year,month,day,"熱量")
//        DrawLine(getDate().toInt()-6,getDate().toInt())
    }
    fun DrawLine(mDate: List<Int>,Type:String)
    {
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "no value").toString()
        var GetCalories = LoginSetting.getString("Calories", "no value").toString().toFloat()
        db.collection("user_info")
            .document(GetID)
            .get()
            .addOnSuccessListener { result1 ->
                db.collection(GetID)
                    .get()
                    .addOnSuccessListener { result ->
                        val linechart = mLineChart

                        val entries: MutableList<Entry> = ArrayList()
                        val entries_S: MutableList<Entry> = ArrayList()
                        var Standard:Float = 0f
                        for (i in 0..mDate.count() - 1) {
                            var Total = 0f
                            for (document in result) {
                                if (document.getString("Date").equals(mDate.get(i).toString())) {
                                    Total += document.get(Type).toString().toFloat()
                                }
                            }
                            //            var x :Int = (i-DateF)+1
                            //            Main2_LineDateL.append(i.toString()+" "+x.toString()+"："+Total.toString()+"\n")
                            entries.add(Entry(i.toFloat() + 1, Total))

                            if (Type == "熱量") {
                                Standard = result1.get("Calories").toString().toFloat()
                                entries_S.add(
                                    Entry(
                                        i.toFloat() + 1,
                                        result1.get("Calories").toString().toFloat()
                                    )
                                )
                            } else {
                                Standard = result1.get(Type).toString().toFloat()
                                entries_S.add(
                                    Entry(
                                        i.toFloat() + 1,
                                        result1.get(Type).toString().toFloat()
                                    )
                                )
                            }

                        }


                        val dataSet = LineDataSet(entries, Type) // add entries to dataset
                        val dataSet_S = LineDataSet(entries_S, "標準份數：" + Standard.toString()) // add entries to dataset

                        if (Type == "熱量")
                        {
                            dataSet.color = ContextCompat.getColor(this, R.color.Calories)
                        }
                        else if (Type == "全榖雜糧類")
                        {
                            dataSet.color = ContextCompat.getColor(this, R.color.Type1)
                        }
                        else if (Type == "豆魚蛋肉類")
                        {
                            dataSet.color = ContextCompat.getColor(this, R.color.Type2)
                        }
                        else if (Type == "蔬菜類")
                        {
                            dataSet.color = ContextCompat.getColor(this, R.color.Type3)
                        }
                        else if (Type == "水果類")
                        {
                            dataSet.color = ContextCompat.getColor(this, R.color.Type4)
                        }
                        else if (Type == "乳品類")
                        {
                            dataSet.color = ContextCompat.getColor(this, R.color.Type5)
                        }
                        else if (Type == "油脂與堅果類")
                        {
                            dataSet.color = ContextCompat.getColor(this, R.color.Type6)
                        }

                        dataSet_S.color = ContextCompat.getColor(this, R.color.Over)


                        dataSet.setCircleColor(R.color.Calories) //圆点颜色
                        dataSet_S.setCircleColor(R.color.Over) //圆点颜色

                        val xAxis: XAxis = mLineChart.getXAxis()
                        xAxis.position = XAxisPosition.BOTTOM
                        xAxis.granularity = 1f
                        xAxis.textSize = 13f

                        val rightAxis: YAxis = mLineChart.getAxisRight() //獲取右側的軸線
                        rightAxis.isEnabled = false //不顯示右側Y軸

                        val leftAxis: YAxis = mLineChart.getAxisLeft()
                        leftAxis.setStartAtZero(true)
                        leftAxis.textSize = 15f

                        dataSet.lineWidth = 5f //线条宽度
                        dataSet.setValueTextSize(10.0f);
                        dataSet_S.lineWidth = 5f //线条宽度
                        dataSet_S.setValueTextSize(0f);
                        //chart设置数据

                        val legend: Legend = mLineChart.getLegend()
                        legend.setTextSize(15f)

                        val lines: MutableList<ILineDataSet> = ArrayList()
                        lines.add(dataSet)
                        lines.add(dataSet_S)

                        val lineData = LineData(lines)
                        //是否绘制线条上的文字
                        lineData.setDrawValues(true)

                        linechart.data = lineData
                        linechart.invalidate() // refresh
                    }
            }
    }

    fun getLineDate(yearF:Int,monthF:Int,dayF:Int,yearL:Int,monthL:Int,dayL:Int,Type:String)
    {
        val cF = Calendar.getInstance()
        cF.set(Calendar.YEAR, yearF)
        cF.set(Calendar.MONTH, monthF)
        cF.set(Calendar.DAY_OF_MONTH, dayF)
        val daysInMonthF: Int = cF.getActualMaximum(Calendar.DAY_OF_MONTH)
        if (yearF == yearL)
        {

            if (monthF < monthL)
            {
                var DayNum :Int = daysInMonthF - dayF +1
                val Date = SimpleDateFormat("yyyyMMdd", Locale.TAIWAN)
                val arrayList_Date = ArrayList<Int>()
                for (d in dayF..daysInMonthF)
                {
                    val cValue = Calendar.getInstance()
                    cValue.set(Calendar.YEAR, yearF)
                    cValue.set(Calendar.MONTH, monthF)
                    cValue.set(Calendar.DAY_OF_MONTH, d)
                    arrayList_Date.add(Date.format(cValue.time).toInt())
                }
                for (d in 1..dayL)
                {
                    val cValue = Calendar.getInstance()
                    cValue.set(Calendar.YEAR, yearL)
                    cValue.set(Calendar.MONTH, monthL)
                    cValue.set(Calendar.DAY_OF_MONTH, d)
                    arrayList_Date.add(Date.format(cValue.time).toInt())
                }

                DrawLine(arrayList_Date,Type)
//            for (a in arrayList_Date)
//            {
//                textView.append(a.toString()+"\n")
//            }
            }
            else if (monthF == monthL)
            {
                var DayNum :Int = daysInMonthF - dayF +1
                val Date = SimpleDateFormat("yyyyMMdd", Locale.TAIWAN)
//            textView.append(DayNum.toString())
                val arrayList_Date = ArrayList<Int>()
                for (d in dayF..dayL)
                {
                    val cValue = Calendar.getInstance()
                    cValue.set(Calendar.YEAR, yearF)
                    cValue.set(Calendar.MONTH, monthF)
                    cValue.set(Calendar.DAY_OF_MONTH, d)
                    arrayList_Date.add(Date.format(cValue.time).toInt())
                }
                DrawLine(arrayList_Date,Type)
            }
        }
        else if (yearF < yearL)
        {

            if (monthF+1==12 && monthL+1 == 1)
            {
                var DayNum :Int = daysInMonthF - dayF +1
                val Date = SimpleDateFormat("yyyyMMdd", Locale.TAIWAN)
                val arrayList_Date = ArrayList<Int>()
                for (d in dayF..daysInMonthF)
                {
                    val cValue = Calendar.getInstance()
                    cValue.set(Calendar.YEAR, yearF)
                    cValue.set(Calendar.MONTH, monthF)
                    cValue.set(Calendar.DAY_OF_MONTH, d)
                    arrayList_Date.add(Date.format(cValue.time).toInt())
                }
                for (d in 1..dayL)
                {
                    val cValue = Calendar.getInstance()
                    cValue.set(Calendar.YEAR, yearL)
                    cValue.set(Calendar.MONTH, monthL)
                    cValue.set(Calendar.DAY_OF_MONTH, d)
                    arrayList_Date.add(Date.format(cValue.time).toInt())
                }

                DrawLine(arrayList_Date,Type)
            }
            else
            {
                Toast.makeText(this, "選取範圍過大", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //六大類折線圖
     private val mListener_Main2_LineCalories = View.OnClickListener {
        if (!Main2_LineDateF.text.toString().equals("選擇起始日期"))
        {
            val yearF:Int = Main2_LineDateF.text.toString().toInt() / 10000
            val monthF:Int = (Main2_LineDateF.text.toString().toInt() - (yearF*10000))/100
            val dayF:Int = (Main2_LineDateF.text.toString().toInt() - (yearF*10000) - (monthF*100))
            val yearL:Int = Main2_LineDateL.text.toString().toInt() / 10000
            val monthL:Int = (Main2_LineDateL.text.toString().toInt() - (yearL*10000))/100
            val dayL:Int = (Main2_LineDateL.text.toString().toInt() - (yearL*10000) - (monthL*100))
            getLineDate(yearF,monthF-1,dayF,yearL,monthL-1,dayL,"熱量")
        }
        else
        {
            Toast.makeText(this, "請先選擇起始日期", Toast.LENGTH_SHORT).show()
        }
    }

    private val mListener_Main2_LineType1 = View.OnClickListener {
        if (!Main2_LineDateF.text.toString().equals("選擇起始日期"))
        {
            val yearF:Int = Main2_LineDateF.text.toString().toInt() / 10000
            val monthF:Int = (Main2_LineDateF.text.toString().toInt() - (yearF*10000))/100
            val dayF:Int = (Main2_LineDateF.text.toString().toInt() - (yearF*10000) - (monthF*100))
            val yearL:Int = Main2_LineDateL.text.toString().toInt() / 10000
            val monthL:Int = (Main2_LineDateL.text.toString().toInt() - (yearL*10000))/100
            val dayL:Int = (Main2_LineDateL.text.toString().toInt() - (yearL*10000) - (monthL*100))
            getLineDate(yearF,monthF-1,dayF,yearL,monthL-1,dayL,"全榖雜糧類")
        }
        else
        {
            Toast.makeText(this, "請先選擇起始日期", Toast.LENGTH_SHORT).show()
        }
    }
    private val mListener_Main2_LineType2 = View.OnClickListener {
        if (!Main2_LineDateF.text.toString().equals("選擇起始日期"))
        {
            val yearF:Int = Main2_LineDateF.text.toString().toInt() / 10000
            val monthF:Int = (Main2_LineDateF.text.toString().toInt() - (yearF*10000))/100
            val dayF:Int = (Main2_LineDateF.text.toString().toInt() - (yearF*10000) - (monthF*100))
            val yearL:Int = Main2_LineDateL.text.toString().toInt() / 10000
            val monthL:Int = (Main2_LineDateL.text.toString().toInt() - (yearL*10000))/100
            val dayL:Int = (Main2_LineDateL.text.toString().toInt() - (yearL*10000) - (monthL*100))
            getLineDate(yearF,monthF-1,dayF,yearL,monthL-1,dayL,"豆魚蛋肉類")
        }
        else
        {
            Toast.makeText(this, "請先選擇起始日期", Toast.LENGTH_SHORT).show()
        }
    }
    private val mListener_Main2_LineType3 = View.OnClickListener {
        if (!Main2_LineDateF.text.toString().equals("選擇起始日期"))
        {
            val yearF:Int = Main2_LineDateF.text.toString().toInt() / 10000
            val monthF:Int = (Main2_LineDateF.text.toString().toInt() - (yearF*10000))/100
            val dayF:Int = (Main2_LineDateF.text.toString().toInt() - (yearF*10000) - (monthF*100))
            val yearL:Int = Main2_LineDateL.text.toString().toInt() / 10000
            val monthL:Int = (Main2_LineDateL.text.toString().toInt() - (yearL*10000))/100
            val dayL:Int = (Main2_LineDateL.text.toString().toInt() - (yearL*10000) - (monthL*100))
            getLineDate(yearF,monthF-1,dayF,yearL,monthL-1,dayL,"蔬菜類")
        }
        else
        {
            Toast.makeText(this, "請先選擇起始日期", Toast.LENGTH_SHORT).show()
        }
    }
    private val mListener_Main2_LineType4 = View.OnClickListener {
        if (!Main2_LineDateF.text.toString().equals("選擇起始日期"))
        {
            val yearF:Int = Main2_LineDateF.text.toString().toInt() / 10000
            val monthF:Int = (Main2_LineDateF.text.toString().toInt() - (yearF*10000))/100
            val dayF:Int = (Main2_LineDateF.text.toString().toInt() - (yearF*10000) - (monthF*100))
            val yearL:Int = Main2_LineDateL.text.toString().toInt() / 10000
            val monthL:Int = (Main2_LineDateL.text.toString().toInt() - (yearL*10000))/100
            val dayL:Int = (Main2_LineDateL.text.toString().toInt() - (yearL*10000) - (monthL*100))
            getLineDate(yearF,monthF-1,dayF,yearL,monthL-1,dayL,"水果類")
        }
        else
        {
            Toast.makeText(this, "請先選擇起始日期", Toast.LENGTH_SHORT).show()
        }
    }
    private val mListener_Main2_LineType5 = View.OnClickListener {
        if (!Main2_LineDateF.text.toString().equals("選擇起始日期"))
        {
            val yearF:Int = Main2_LineDateF.text.toString().toInt() / 10000
            val monthF:Int = (Main2_LineDateF.text.toString().toInt() - (yearF*10000))/100
            val dayF:Int = (Main2_LineDateF.text.toString().toInt() - (yearF*10000) - (monthF*100))
            val yearL:Int = Main2_LineDateL.text.toString().toInt() / 10000
            val monthL:Int = (Main2_LineDateL.text.toString().toInt() - (yearL*10000))/100
            val dayL:Int = (Main2_LineDateL.text.toString().toInt() - (yearL*10000) - (monthL*100))
            getLineDate(yearF,monthF-1,dayF,yearL,monthL-1,dayL,"乳品類")
        }
        else
        {
            Toast.makeText(this, "請先選擇起始日期", Toast.LENGTH_SHORT).show()
        }
    }
    private val mListener_Main2_LineType6 = View.OnClickListener {
        if (!Main2_LineDateF.text.toString().equals("選擇起始日期"))
        {
            val yearF:Int = Main2_LineDateF.text.toString().toInt() / 10000
            val monthF:Int = (Main2_LineDateF.text.toString().toInt() - (yearF*10000))/100
            val dayF:Int = (Main2_LineDateF.text.toString().toInt() - (yearF*10000) - (monthF*100))
            val yearL:Int = Main2_LineDateL.text.toString().toInt() / 10000
            val monthL:Int = (Main2_LineDateL.text.toString().toInt() - (yearL*10000))/100
            val dayL:Int = (Main2_LineDateL.text.toString().toInt() - (yearL*10000) - (monthL*100))
            getLineDate(yearF,monthF-1,dayF,yearL,monthL-1,dayL,"油脂與堅果類")
        }
        else
        {
            Toast.makeText(this, "請先選擇起始日期", Toast.LENGTH_SHORT).show()
        }
    }

}


