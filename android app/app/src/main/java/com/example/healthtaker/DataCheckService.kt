package com.example.healthtaker

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import org.json.JSONArray
import org.json.JSONObject
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.thread

class DataCheckService: Service() {

    var data = ArrayList<JSONObject>()
    var id = ""

    companion object {
        var instance: DataCheckService? = null
    }

    init {
        instance = this
    }

    override fun onStart(intent: Intent?, startId: Int) {

    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        id = intent?.extras?.getString("ID")!!
        thread {
            data = JSONArray(API.get("getNewFall/$id")).let{ x ->
                Array(x.length()) {x.getJSONObject(it)}.toCollection(ArrayList<JSONObject>())
            }
        }
        val resultIntent = Intent(this, SelectUserActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        Timer().schedule(object: TimerTask() {
            override fun run() {

                var res = JSONArray(API.get("getNewFall/$id")).let{ x ->
                    Array(x.length()) {x.getJSONObject(it)}.toCollection(ArrayList<JSONObject>())
                }

                res.forEach {
                    if (!data.any { x -> x.getString("ID") == it.getString("ID")}) {
                        val notification = NotificationCompat.Builder(applicationContext,"fall_dataCheck")
                            .setContentTitle("Health Taker Emergency Notification")
                            .setContentText("${it.getString("CareRecipient")} has fallen\nPlease help him/her")
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setPriority(NotificationManager.IMPORTANCE_HIGH)
                            .setContentIntent(resultPendingIntent)
                            .setWhen(System.currentTimeMillis())
                            .setAutoCancel(true)
                            .build()
                        startForeground(1, notification)
                    }
                }

                data = res
            }
        }, 20000, 20000)

        startForeground(2,  NotificationCompat.Builder(applicationContext,"fall_dataCheck")
            .build()
        )

        return START_STICKY
    }
}