package top.wefor.season.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import top.wefor.season.R

class SimpleMusicNotification(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "simple_music_channel"
        private const val NOTIFICATION_ID = 123
        private const val TAG = "MusicNotification"
    }

    val KEY_PLAY = "ACTION_PLAY"
    val KEY_PAUSE = "ACTION_PAUSE"

    private val playIntent: PendingIntent by lazy { createBroadcastIntent( KEY_PLAY, 0) }
    private val pauseIntent: PendingIntent by lazy { createBroadcastIntent(KEY_PAUSE, 1) }

    val contentIntent: PendingIntent = PendingIntent.getActivity(
        context,
        0,
        context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    init {
        createNotificationChannel()
    }

    /**
     * 创建通知通道 (适配 Android 8.0+)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Simple Music Playback Controls"
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }

    /**
     * 显示音乐播放通知
     */
    fun showNotification(title: String, artist: String, isPlaying: Boolean) {
        Log.d(TAG, "Showing music notification: $title by $artist")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setContentTitle(title)
            .setContentText(artist)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(contentIntent) // 点击通知栏时回到 App
            .setAutoCancel(false) // 不自动取消
            .addAction(
                if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24,
                if (isPlaying) "Pause" else "Play",
                if (isPlaying) pauseIntent else playIntent
            )
            .setOngoing(isPlaying) // 锁定通知，防止被滑动删除
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0) // 显示紧凑模式下的第一个按钮 (播放/暂停)
            )
            .build()

        // 显示通知
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ActivityCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } else {
            Log.w(TAG, "Missing POST_NOTIFICATIONS permission, cannot show notification")
        }
    }

    /**
     * 取消音乐通知
     */
    fun cancelNotification() {
        Log.d(TAG, "Canceling music notification")
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }

    /**
     * 创建 PendingIntent 用于广播操作
     */
    private fun createBroadcastIntent(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, MusicActionReceiver::class.java).setAction(action)
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}