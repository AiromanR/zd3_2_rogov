package com.example.zd3_1_rogov

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText

lateinit var email: EditText
lateinit var password: EditText

class SignActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
    }
    fun Login(view: View){
        if(email.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()){
        val intent = Intent(this, QuestsActivity::class.java)
        startActivity(intent)
        }
        else{
            val alert = AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("У вас есть пустые поля")
                .setPositiveButton("OK", null)
                .create()
                .show()
        }
    }
}