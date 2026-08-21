package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

class StudyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "🎯 وقت المذاكرة مع StudySnap AI"
        val message = intent.getStringExtra("message") ?: "حافظ على سلسلة دراستك وحل أسئلة اليوم لتثبيت المفاهيم الصعبة!"
        NotificationHelper.sendNotification(context, title, message)
        
        // Schedule next reminder
        NotificationHelper.scheduleRecurringStudyReminders(context)
    }
}

object NotificationHelper {
    const val CHANNEL_ID = "studysnap_study_reminders"
    const val CHANNEL_NAME = "تنبيهات المذاكرة والتحفيز"
    private const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات تذكيرية يومية للمذاكرة وحل الأسئلة وتحفيز الطلاب"
                enableVibration(true)
                enableLights(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(context: Context, title: String, message: String) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        try {
            val manager = NotificationManagerCompat.from(context)
            manager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun scheduleRecurringStudyReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val reminders = listOf(
            "📚 وقت مراجعة الأسئلة الصعبة!" to "التقط صورة لأي مسألة تقف أمامك ودع الذكاء الاصطناعي يشرحها لك خطوة بخطوة.",
            "⚡ حافظ على نشاطك الدراسي اليومي!" to "استغل وقتك الآن وحل مسألة جديدة لتقوية مستواك.",
            "🎯 لا تدع درساً يفوتك!" to "المساعد الذكي جاهز للإجابة عن أسئلتك في الرياضيات والفيزياء والكيمياء.",
            "🏆 إنجاز اليوم يصنع تفوق الغد!" to "هل راجعت نقاط ضعفك اليوم؟ افتح StudySnap واستعرض خطة المذاكرة."
        )

        val randomReminder = reminders.random()

        val intent = Intent(context, StudyReminderReceiver::class.java).apply {
            putExtra("title", randomReminder.first)
            putExtra("message", randomReminder.second)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            100,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule after 4 hours from now
        val triggerAt = System.currentTimeMillis() + (4 * 60 * 60 * 1000L)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
        } catch (e: Exception) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }
}
