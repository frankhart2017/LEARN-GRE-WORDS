package com.vastukosh.grewords

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.ActionBar
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vastukosh.grewords.R.id.*
import kotlinx.android.synthetic.main.activity_mnemonic.*
import org.json.JSONException

class MnemonicActivity : AppCompatActivity() {

    lateinit var toolbar: ActionBar

    var URL = "https://www.jncpasighat.edu.in/temp/gre/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mnemonic)

        progressCircle.visibility = View.GONE

        toolbar = supportActionBar!!
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)

        bottomNavigation.itemIconTintList = null

        fetchFirst()

        bottomNavigation.setOnNavigationItemReselectedListener {
            when(it.itemId) {
                navigation_next -> {
                    var wordVal = wordTextView.text.toString()
                    wordVal = wordVal.replace("Word:\n", "")
                    val url = "$URL?next=1&word=$wordVal"

                    Log.d("ERRORCODE", url)
                    Log.d("ERRORCODE", wordTextView.text.toString())

                    val loginRequest = @SuppressLint("SetTextI18n")
                    object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                        try {

                            val status = response.getInt("status")

                            if (status == 0) {
                                Toast.makeText(this, "Oops, server error!", Toast.LENGTH_SHORT).show()
                            } else {
                                finish()
                                startActivity(intent)
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
                navigation_test -> {
                    val testIntent = Intent(this, TestActivity::class.java)
                    startActivity(testIntent)
                }
                navigation_search -> {
                    val searchIntent = Intent(this, SearchActivity::class.java)
                    startActivity(searchIntent)
                }
            }
        }

    }

    fun fetchFirst() {
        val url = "$URL?fetch=1"

        Log.d("ERROR", url)

        progressCircle.visibility = View.VISIBLE

        val loginRequest = @SuppressLint("SetTextI18n")
        object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
            try {
                val word = response.getString("word")
                val meaning = response.getJSONArray("meaning")
                val mnemonic = response.getJSONArray("mnemonic")
                val status = response.getInt("status")
                val count = response.getInt("count")

                if(status == 2) {
                    Toast.makeText(this, "Take a test, 30 words done", Toast.LENGTH_SHORT).show()
                } else {
                    var i = 0
                    var j = 0
                    var strMeaning = "Meaning: \n"
                    var strMnemonic = "Mnemonic: \n"
                    while(i < meaning.length()) {
                        if(meaning.get(i).toString().length > 1) {
                            strMeaning += (j + 1).toString() + ") " + meaning.get(i).toString() + "\n"
                            j++
                        }
                        i++
                    }
                    strMeaning += "\n\n\n\n" + count.toString() + " left!"
                    i = 0
                    j = 0
                    while(i < mnemonic.length()) {
                        if(mnemonic.get(i).toString().length > 1) {
                            strMnemonic += (j + 1).toString() + ")" + mnemonic.get(i).toString() + "\n"
                            j++
                        }
                        i++
                    }

                    wordTextView.text = word
                    mnemonicTextView.text = strMnemonic
                    meaningTextView.text = strMeaning

                    progressCircle.visibility = View.GONE
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
