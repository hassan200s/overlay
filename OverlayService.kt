package com.example.overlayapp

import android.app.Service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout

class OverlayService : Service() {

    private var rootView: FrameLayout? = null
    private var windowManager: WindowManager? = null

    private val X1_DP = 170
    private val X2_DP = 370
    private val BAR_WIDTH_DP = 8
    private val BAR_ALPHA = 0.6f

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        rootView = FrameLayout(this)

        fun dpToPx(dp: Int): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()

        val bar1 = View(this).apply {
            layoutParams = FrameLayout.LayoutParams(dpToPx(BAR_WIDTH_DP), FrameLayout.LayoutParams.MATCH_PARENT).also { it.leftMargin = dpToPx(X1_DP) }
            setBackgroundColor(Color.argb((BAR_ALPHA*255).toInt(), 255,255,255))
        }
        val bar2 = View(this).apply {
            layoutParams = FrameLayout.LayoutParams(dpToPx(BAR_WIDTH_DP), FrameLayout.LayoutParams.MATCH_PARENT).also { it.leftMargin = dpToPx(X2_DP) }
            setBackgroundColor(Color.argb((BAR_ALPHA*255).toInt(), 255,255,255))
        }

        val stopBtn = Button(this).apply {
            text = "إيقاف"
            setOnClickListener { stopSelf() }
        }
        val btnParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM or Gravity.END)
        val m = dpToPx(16); btnParams.setMargins(m,m,m,m)

        rootView?.addView(bar1); rootView?.addView(bar2); rootView?.addView(stopBtn, btnParams)

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,
            android.graphics.PixelFormat.TRANSLUCENT).apply { gravity = Gravity.TOP or Gravity.START }

        windowManager?.addView(rootView, params)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "overlay_service_channel"
            val channel = NotificationChannel(channelId, "Overlay Service", NotificationManager.IMPORTANCE_LOW)
            val nm = getSystemService(NotificationManager::class.java)
            nm?.createNotificationChannel(channel)
            val notification: Notification = android.app.Notification.Builder(this, channelId).setContentTitle("Overlay يعمل").setContentText("شريطان أبيضان شفافان").setSmallIcon(android.R.drawable.ic_menu_view).build()
            startForeground(1, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        rootView?.let { windowManager?.removeView(it) }
        rootView = null
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
