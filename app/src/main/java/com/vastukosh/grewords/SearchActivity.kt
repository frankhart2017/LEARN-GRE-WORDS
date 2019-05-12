package com.vastukosh.grewords

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vastukosh.grewords.R.id.*
import kotlinx.android.synthetic.main.activity_mnemonic.*
import kotlinx.android.synthetic.main.activity_search.*
import org.json.JSONException

class SearchActivity : AppCompatActivity() {

    var URL = "https://www.jncpasighat.edu.in/temp/gre/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        progressCircleSearch.visibility = View.GONE

        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)

        bottomNavigation.itemIconTintList = null

        bottomNavigation.setOnNavigationItemReselectedListener {
            when(it.itemId) {
                navigation_test -> {
                    val testIntent = Intent(this, TestActivity::class.java)
                    startActivity(testIntent)
                }
                navigation_mnemonic -> {
                    val mnemonicActivity = Intent(this, MnemonicActivity::class.java)
                    startActivity(mnemonicActivity)
                }
            }
        }
    }

    fun searchFunction(view: View) {
        if(!searchWordEditText.text.isEmpty()) {

            progressCircleSearch.visibility = View.VISIBLE

            val url = "$URL?search=1&word=" + devoidOfSpace(searchWordEditText.text.toString().toLowerCase())

            Log.d("ERRORD", url)

            val loginRequest = @SuppressLint("SetTextI18n")
            object: JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                try {

                    val status = response.getInt("status")
                    val word = response.getString("word")
                    val meaning = response.getJSONArray("meaning")

                    searchWordEditText.text.clear()

                    progressCircleSearch.visibility = View.GONE

                    if(status == -1) {
                        searchResultTextView.text = "Word: $word, not found in the database!"
                    } else {
                        var statusString = "Status: Not learnt!"
                        if(status == 1) {
                           statusString = "Status: In quiz!"
                        } else if(status == 2) {
                            statusString = "Status: Learnt!"
                        }

                        var meaningString = "Meaning: \n"

                        var i=0
                        var j=0
                        while(i < meaning.length()) {
                            if(meaning.get(i).toString().length > 1) {
                                meaningString += (j + 1).toString() + ") " + meaning.get(i).toString() + "\n"
                                j++
                            }
                            i++
                        }

                        searchResultTextView.text = meaningString + "\n" + statusString
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
        } else {
            Toast.makeText(this, "Enter word to search", Toast.LENGTH_SHORT).show()
        }
    }

    fun devoidOfSpace(str: String): String {
        return str.replace("\\s".toRegex(), "%20")
    }
}
