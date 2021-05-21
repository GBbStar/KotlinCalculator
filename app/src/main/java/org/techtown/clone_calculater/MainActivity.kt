package org.techtown.clone_calculater

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.room.Room
import org.techtown.clone_calculater.model.CalculateHistory
import java.lang.NumberFormatException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var operationCode = listOf<Char>( '*', '/', '+', '-', '(', ')') //연산 부호
    var postfixList:ArrayList<String> = ArrayList() //후위표기법으로 변환 후 저장 할 ArrayList
    var opStack = Stack<Char>() // 연산 부호 우선순위처리 하며 후위 표기법으로 변경하는 Stack
    var calculatorStack = Stack<String>() //후위 표기법을 계산하는 Stack
    var index = 0;//content.substring() 인수


    fun calculate(content:String):String{
        for ( i in content) {
            for (j in operationCode) {
                if (i == j) { //문자열과 연산 부호 비교
                    //postfixList에 연산 부호가 나오기 전까지의 숫자를 담는다(공백제거)
                    postfixList.add(
                        content.substring(index, content.indexOf(i)).trim().replace("(", "")
                            .replace(")", "")
                    )
                    if (i == '(') {
                        if (i == ')') {//우 괄호가 나오면 좌 괄호가 나오거나 스택에 비어있을때 까지 pop하여 list에 저장
                            while (true) {
                                postfixList.add(opStack.pop().toString())
                                if (opStack.pop() == '(' || opStack.isEmpty()) {
                                    break
                                }
                            }
                        }
                    }

                    if (opStack.isEmpty()) { //opStack이 비어 있을 경우
                        opStack.push(j) //연산 부호 저장
                    } else { //opStack이 비어 있지 않을 경우
                        if (opOrder(j) > opOrder(opStack.peek())) { //우선 순위 비교
                            opStack.push(j) //스택에 top 값 보다 높은 우선순위이면 그대로 저장
                        } else if (opOrder(j) <= opOrder(opStack.peek())) {//우선 순위 비교
                            postfixList.add(
                                opStack.peek().toString()
                            )//스택에 있는 값이 우선순위가 같거나 작을 경우 list에 저장
                            opStack.pop()//스택 제거
                            opStack.push(j)//높은 우선순위 연산 부호 스택에 저장
                        }
                    }
                    index = content.indexOf(i) + 1// 다음 순서 처리
                }
            }
        }
        postfixList.add(
            content.substring(index, content.length).trim().replace("(", "").replace(")", "")
        ); //마지막 숫자 처리

        if (!opStack.isEmpty()) { //Stack에 남아있는 연산 모두 postfixList에 추가
            for (i in opStack) {
                postfixList.add(opStack.peek().toString())
                opStack.pop()
            }
        }

        //list에 공백, 괄호 제거
        for (i in postfixList){
            if (i == "") {
                postfixList.remove(i)
            }
            else if (i== "(") {
                postfixList.remove(i)
            }
            else if (i == ")") {
                postfixList.remove(i)
            }
        }

        opStack.clear() //Stack 비우기

        //postfixList를 calculatorStack에 저장하면서 후위연산 처리

        for (i in postfixList) {
            calculatorStack.push(i)

            for (j in operationCode) {
                if (i[0] == j) { //연산 부호 비교
                    calculatorStack.pop() //stack에 저장된 연산 부호 제거
                    var s2:Double = 0.0
                    var s1:Double = 0.0 //stack에서 pop 되는 값들을 저장할 변수
                    var rs = ""// 연산 처리 후 문자열로 변환 후 stack에 저장할 변수

                    s2 = calculatorStack.pop().toDouble() //스택에서 pop하여 문자열을 숫자로 형변환
                    s1 = calculatorStack.pop().toDouble()

                    //연산 부호에 해당하는 산술 처리 후 stack에 저장
                    when(j) {
                        '+' -> {
                            rs = (s1 + s2).toString()
                            calculatorStack.push(rs)
                        }
                        '-' -> {
                            rs = (s1 - s2).toString()
                            calculatorStack.push(rs)
                        }
                        '*' -> {
                            rs = (s1 * s2).toString()
                            calculatorStack.push(rs)
                        }
                        '/' -> {
                            rs = (s1 / s2).toString()
                            calculatorStack.push(rs)
                        }
                    }
                }
            }
        }

        var re = calculatorStack.peek().toDouble() //Stack Top 데이터
        var result = String.format("%.10f", re) //소수점 10째짜리

        //정수 부분 자리 구하기
        var num = 0
        for (i in 0..result.length) {
            if (result[i] == '.') {
                num = i;
                break;
            }
        }

        //정수부분
        var mok = result.substring(0, num)

        //나머지 연산
        var divde = result.toDouble() % mok.toDouble()

        //나머지가 0이면 소수점 자릿 수 안보이게
        if (divde == 0.0) {
            result = String.format("%.0f", re);
        }

        return result
    }

    private fun opOrder(op:Char):Int {
        when (op) {
            '+', '-'-> return 1
            '*', '/' -> return 2
            else -> return -1
        }
    }

    private val expressionTextView: TextView by lazy{
        findViewById<TextView>(R.id.expressionTextView)
    }

    private val resultTextView: TextView by lazy{
        findViewById<TextView>(R.id.resultTextView)
    }

    private val historyLayout: View by lazy{
        findViewById<View>(R.id.historyLayout)
    }

    private val historyLinearLayout: LinearLayout by lazy {
        findViewById<LinearLayout>(R.id.histiryLinearLayout)
    }

    lateinit var mdb:AppRoomDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mdb = Room.databaseBuilder(
            applicationContext,
            AppRoomDatabase::class.java,
            "historyDB"
        ).build()


    }

    fun buttonClicked(v: View){
        when(v.id){
            R.id.button0 -> numberButtonClicked("0")
            R.id.button1 -> numberButtonClicked("1")
            R.id.button2 -> numberButtonClicked("2")
            R.id.button3 -> numberButtonClicked("3")
            R.id.button4 -> numberButtonClicked("4")
            R.id.button5 -> numberButtonClicked("5")
            R.id.button6 -> numberButtonClicked("6")
            R.id.button7 -> numberButtonClicked("7")
            R.id.button8 -> numberButtonClicked("8")
            R.id.button9 -> numberButtonClicked("9")
            R.id.buttonPlus -> operatorButtonClicked("+")
            R.id.buttonMinus -> operatorButtonClicked("-")
            R.id.buttonMulti -> operatorButtonClicked("*")
            R.id.buttonDivider -> operatorButtonClicked("/")
            R.id.buttonModulo -> operatorButtonClicked("%")
            R.id.buttonParenthesis -> checkParenthesis()
        }
    }

    private fun checkParenthesis(){
        if (expressionTextView.text.isEmpty() || expressionTextView.text.last() == '('){
            operatorButtonClicked("(")
        }
        else{
            operatorButtonClicked(")")
        }
    }

    private fun numberButtonClicked(number:String){
        expressionTextView.append(number)


//        resultTextView.text = calculateExpression()
    }

    private fun operatorButtonClicked(operator:String){
        Log.d("operator",operator)

        expressionTextView.append(operator)

//        val ssb = SpannableStringBuilder(expressionTextView.text)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ssb.setSpan(
//                ForegroundColorSpan(getColor(R.color.green)),
//                expressionTextView.text.length-1,
//                expressionTextView.text.length,
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
//        }
//
//        expressionTextView.text = ssb

    }

    private fun calculateExpression():String{
        var expressionTexts = expressionTextView.text.toString().replace(" ", "")

        Log.d("calculate", calculate(expressionTexts))

        return expressionTexts

    }

    fun String.isNumber():Boolean{
        return try{
            this.toBigInteger()
            true
        } catch (e: NumberFormatException){
            false
        }
    }



    fun resultButtonClicked(v: View){
        val expressionText = expressionTextView.text.split(" ")




//        Thread(Runnable {
//            mdb.calculateHistoryDao().insertCalculateHistory(CalculateHistory(null, expression, resultText))
//        }).start()
//
//        resultTextView.text=""
//        expressionTextView.text=resultText




    }

    fun clearButtonClicked(v: View){
        expressionTextView.text = ""
        resultTextView.text = ""

    }

    fun historyButtonClicked(v: View){
        historyLayout.isVisible = true
        historyLinearLayout.removeAllViews()

        Thread(Runnable {
            mdb.calculateHistoryDao().getAll().reversed().forEach {
                runOnUiThread{
                    val historyView = LayoutInflater.from(this).inflate(R.layout.history_row, null, false)
                    historyView.findViewById<TextView>(R.id.expressionTextView).text = it.expression
                    historyView.findViewById<TextView>(R.id.resultTextView).text = "= ${it.result}"

                    historyLinearLayout.addView(historyView)
                }
            }
        }).start()
    }

    fun closeHistoryButtonClicked(v: View){
        historyLayout.isVisible = false
    }

    fun historyClearButtonClicked(v: View){
        historyLinearLayout.removeAllViews()

        Thread(Runnable {
            mdb.calculateHistoryDao().deleteAll()
        }).start()
    }


}