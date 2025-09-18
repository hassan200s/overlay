package com.example.overlayapp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val start = Button(context).apply { text = "تشغيل الـ Overlay" }
            val stop = Button(context).apply { text = "إيقاف" }
            addView(start)
            addView(stop)
            start.setOnClickListener {
                if (canDrawOverlays()) {
                    startService(Intent(this@MainActivity, OverlayService::class.java))
                } else {
                    requestOverlayPermission()
                }
            }
            stop.setOnClickListener {
                stopService(Intent(this@MainActivity, OverlayService::class.java))
            }
        }
        setContentView(layout)
    }

    private fun canDrawOverlays(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else true
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            Toast.makeText(this, "يرجى منح إذن العرض فوق التطبيقات", Toast.LENGTH_LONG).show()
        }
    }
}
