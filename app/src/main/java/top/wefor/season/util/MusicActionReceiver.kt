package top.wefor.season.util

import android.R.attr.action
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class MusicActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 发送本地广播
        val localIntent = Intent("ACTION_MUSIC_CONTROL")
        localIntent.putExtra("ACTION_TYPE", intent.action) // 传递具体操作类型

        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
    }
}