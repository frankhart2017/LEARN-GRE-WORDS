package com.vastukosh.grewords

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.ActionBar
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_test.*
import org.json.JSONException
import com.vastukosh.grewords.R.id.navigation_submit
import kotlinx.android.synthetic.main.activity_mnemonic.*

class TestActivity : AppCompatActivity() {

    lateinit var sp: SharedPreferences
    lateinit var toolbar: ActionBar

    var URL = "https://www.jncpasighat.edu.in/temp/gre/"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        progressCircleTest.visibility = View.GONE

        showButton.visibility = View.GONE
        wrongButton.visibility = View.GONE
        correctButton.visibility = View.GONE

        toolbar = supportActionBar!!
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)

        val url = "$URL?numTestQuestions=1"

        sp = getSharedPreferences("login", Context.MODE_PRIVATE)

        bottomNavigation.setOnNavigationItemReselectedListener {
            when(it.itemId) {
                R.id.navigation_submit -> {
                    val score_final = "Final score = " + sp.getInt("score", 0) +
                            " out of " + sp.getInt("questionCount", 0)
                    if(meaningTestTextView.text == score_final) {
                        val mnemonicIntent = Intent(this, MnemonicActivity::class.java)
                        startActivity(mnemonicIntent)
                    }
                    meaningTestTextView.visibility = View.VISIBLE
                    meaningTestTextView.text = score_final
                }
                R.id.navigation_mnemonic -> {
                    val mnemonicIntent = Intent(this, MnemonicActivity::class.java)
                    startActivity(mnemonicIntent)
                }
            }
        }

        val loginRequest = @SuppressLint("SetTextI18n")
        object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
            try {
                val qc = response.getInt("count")
                sp.edit().putInt("questionCount", qc).apply()
                fetchQuestion(sp.getInt("questionCount", 0))

            } catch (e: JSONException) {
                Log.d("JSON", "EXC: " + e.localizedMessage)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not login user: $error")
        }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }
        Volley.newRequestQueue(this).add(loginRequest)

        sp.edit().putInt("score", 0).apply()
    }

    fun fetchQuestion(questionCount: Int) {
        val url = "$URL?testQuestions=1"

        Log.d("SCORE", sp.getInt("score", 0).toString())

        progressCircleTest.visibility = View.VISIBLE

        val loginRequest = @SuppressLint("SetTextI18n")
        object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
            try {
                val word = response.getString("word")
                val count = response.getInt("count")
                val meaning = response.getJSONArray("meaning")

                if(count == 0) {
                    Toast.makeText(this, "No questions!", Toast.LENGTH_SHORT).show()
                } else {
                    var i=0
                    var j=0
                    var strMeaning = ""
                    while(i < meaning.length()) {
                        if(meaning.get(i).toString().length > 1) {
                            strMeaning += (j + 1).toString() + ") " + meaning.get(i).toString() + "\n"
                            j++
                        }
                        i++
                    }

                    val questionNumber = questionCount - count + 1

                    questionNumTextView.text = "Question $questionNumber out of $questionCount"
                    wordQuestionTextView.text = word
                    meaningTestTextView.text = strMeaning
                    meaningTestTextView.visibility = View.INVISIBLE

                    showButton.visibility = View.VISIBLE
                    wrongButton.visibility = View.VISIBLE
                    correctButton.visibility = View.VISIBLE
                }

                progressCircleTest.visibility = View.GONE


            } catch (e: JSONException) {
                Log.d("JSON", "EXC: " + e.localizedMessage)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not login user: $error")
        }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }
        Volley.newRequestQueue(this).add(loginRequest)
    }

    fun showFunction(view: View) {
        meaningTestTextView.visibility = View.VISIBLE
    }

    fun wrongFunction(view: View) {
        val url = "$URL?wrong=1&word=" + wordQuestionTextView.text


        val loginRequest = @SuppressLint("SetTextI18n")
        object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
            try {

                val status = response.getInt("status")

                if(status == 1) {
                    fetchQuestion(sp.getInt("questionCount", 0))
                } else {
                    Toast.makeText(this, "Oops, server error!", Toast.LENGTH_SHORT).show()
                }

            } catch (e: JSONException) {
                Log.d("JSON", "EXC: " + e.localizedMessage)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not login user: $error")
        }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }
        Volley.newRequestQueue(this).add(loginRequest)
    }

    fun correctFunction(view: View) {
        val url = "$URL?correct=1&word=" + wordQuestionTextView.text


        val loginRequest = @SuppressLint("SetTextI18n")
        object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
            try {

                val status = response.getInt("status")

                if(status == 1) {
                    val score = sp.getInt("score", 0)
                    sp.edit().putInt("score", score+1).apply()
                    fetchQuestion(sp.getInt("questionCount", 0))
                } else {
                    Toast.makeText(this, "Oops, server error!", Toast.LENGTH_SHORT).show()
                }

            } catch (e: JSONException) {
                Log.d("JSON", "EXC: " + e.localizedMessage)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not login user: $error")
        }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }
        Volley.newRequestQueue(this).add(loginRequest)
    }
}
