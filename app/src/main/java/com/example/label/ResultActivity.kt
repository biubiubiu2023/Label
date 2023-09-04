package com.example.label

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.label.utils.ViewUtils

/**
 * 创建时间：2023/9/3
 * 编写人： 陈陈陈
 * 功能描述：
 */
class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val image = findViewById<ImageView>(R.id.image)
        val decodeFile = BitmapFactory.decodeFile(ViewUtils.getClipCachePath() + "out.jpg")
        image.setImageBitmap(decodeFile)
    }
}