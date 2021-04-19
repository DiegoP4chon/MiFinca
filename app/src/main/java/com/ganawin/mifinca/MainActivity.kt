package com.ganawin.mifinca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val info = intent.getStringExtra("URL")
        info?.let {
            Toast.makeText(this, "Visita: $info", Toast.LENGTH_LONG).show()
        }
    }
}