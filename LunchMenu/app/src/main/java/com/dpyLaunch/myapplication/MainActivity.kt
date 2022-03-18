package com.dpyLaunch.myapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var textView = findViewById<TextView>(R.id.Today)

        textView.text = ""

        val button: Button = findViewById(R.id.btn_refresh)

        button.setOnClickListener {
            //함수를 적어 넣으세요
            //ActivityCompat.requestPermissions(this, requiredPermissions, multiplePermissionsCode)

            var thread = NetworkThread()
            thread.start()
        }
    }

    inner class NetworkThread : Thread() {
        override fun run() {
            val currentTime: Long = System.currentTimeMillis()
            val dataFormat = SimpleDateFormat("yyyyMMdd")
            val dataFormat2 = SimpleDateFormat("yyyyMM")

            var site = "https://open.neis.go.kr/hub/mealServiceDietIn" +
                    "fo?KEY=683675e9fca048e09efead147be6f933&" +
                    "Type=json&pIndex=1&pSize=100&ATPT_OFCDC_SC_" +
                    "CODE=J10&SD_SCHUL_CODE=7541019&MLSV_FROM_YMD=" +
                    dataFormat2.format(currentTime) + "00"

            var url = URL(site)
            var conn = url.openConnection()
            var input = conn.getInputStream()
            var isr = InputStreamReader(input)

            var br = BufferedReader(isr)

            var str: String? = null
            var buf = StringBuffer()

            do {
                str = br.readLine()

                if (str != null) {
                    buf.append(str)
                }
            } while (str != null)

            var root = JSONObject(buf.toString())

            var real_root =
                root.getJSONArray("mealServiceDietInfo").getJSONObject(1).getJSONArray("row")




            for (i in 0..real_root.length() - 1) {
                var menu: String = (real_root[i] as org.json.JSONObject).getString("DDISH_NM")
                var kcal: String = (real_root[i] as org.json.JSONObject).getString("CAL_INFO")
                var fullday: String = (real_root[i] as org.json.JSONObject).getString("MLSV_YMD")

                var month = fullday.slice(IntRange(4, 5))
                month = month.replace("0", "")

                var day = fullday.slice(IntRange(6, 7))
                if (day[0] == '0') {
                    day = day.replace("0", "")
                }

                if (dataFormat.format(currentTime) == fullday) {
                    runOnUiThread {
                        menu = menu.replace("<br/>", "\n")

                        var temp_str = StringBuilder()
                        temp_str.append("☆오늘급식☆\n")
                        temp_str.append("==================\n")

                        temp_str.append(month + "월 ")
                        temp_str.append(day + "일 ")
                        temp_str.append(GetDay(fullday) + "\n")

                        temp_str.append(menu)
                        temp_str.append("\n")
                        temp_str.append(kcal)
                        temp_str.append("\n")
                        temp_str.append("==================")

                        temp_str.append("\n")
                        temp_str.append("\n")
                        temp_str.append("\n")

                        temp_str.append(Today.text)

                        Today.text = temp_str
                    }
                }

                runOnUiThread {
                    menu = menu.replace("<br/>", "\n")

                    Today.append(month + "월 ")
                    Today.append(day + "일 ")
                    Today.append(GetDay(fullday))

                    Today.append("\n")

                    Today.append(menu)

                    Today.append("\n")
                    Today.append(kcal)
                    Today.append("\n")
                    Today.append("\n")
                    Today.append("\n")
                }

            }
            //text.append("count: ${count}\n")

        }
    }

    fun GetDay(inputDate : String): String {
        val dateFormat: DateFormat = SimpleDateFormat("yyyyMMdd")

        val date: Date = dateFormat.parse(inputDate)


        val calendar = Calendar.getInstance()

        calendar.setTime(date)

        val ddaayy :Int = calendar.get(Calendar.DAY_OF_WEEK)

        when (ddaayy) {
            1 -> return "일요일"
            2 -> return "월요일"
            3 -> return "화요일"
            4 -> return "수요일"
            5 -> return "목요일"
            6 -> return "금요일"
            7 -> return "토요일"
        }
        return "오류"
    }
}