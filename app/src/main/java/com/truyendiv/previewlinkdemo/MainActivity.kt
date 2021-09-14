package com.truyendiv.previewlinkdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    private val openGraphParser by lazy { OpenGraphParser() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.buttonPreview).setOnClickListener {
            fetchInfoUrl()
        }
    }

    private fun fetchInfoUrl() {
        val url = findViewById<EditText>(R.id.editTextUrl).text.toString()
        openGraphParser.parse(url) {
            showPreview(it)
        }
    }

    private fun showPreview(result: OpenGraphResult) {
        with(result) {
            findViewById<TextView>(R.id.textTitle).text = title
            findViewById<TextView>(R.id.textDescription).text = description
            findViewById<TextView>(R.id.textSiteName).text = siteName
            val imageViewUrl = findViewById<ImageView>(R.id.imageUrl)
            Glide.with(this@MainActivity).load(imageUrl).into(imageViewUrl)
        }
    }
}
