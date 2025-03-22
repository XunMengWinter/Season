package top.wefor.season

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.media.SoundPool
import androidx.annotation.RawRes
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import okhttp3.Cache
import top.wefor.season.ui.MainActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.system.exitProcess

/**
 * Created on 2018/11/27.
 *
 * @author ice
 */
class App : Application() {
    private var soundPool: SoundPool? = null

    // Typefaces
//    private val typefaces = mutableMapOf<String, Typeface?>()
    private lateinit var mTypefaceIsabella: Typeface
    private lateinit var mTypefaceRadam: Typeface
    private lateinit var mTypefaceFangzheng: Typeface
    private lateinit var mTypefaceDidot: Typeface
    private lateinit var mTypefaceBookItalic: Typeface

    var isNeedExitApp: Boolean = false

    override fun onCreate() {
        super.onCreate()

        sApp = this
        Logger.addLogAdapter(AndroidLogAdapter())

        val httpCacheDirectory = File(cacheDir, "responses")
        val cacheSize = 10 * 1024 * 1024L // 10 MiB
        httpCache = Cache(httpCacheDirectory, cacheSize)

        mTypefaceIsabella = Typeface.createFromAsset(getAssets(), "fonts/Isabella.ttf")
        mTypefaceRadam = Typeface.createFromAsset(getAssets(), "fonts/Radam.otf")
        mTypefaceFangzheng = Typeface.createFromAsset(getAssets(), "fonts/FangzhengBoyaSong.TTF")
        mTypefaceDidot = Typeface.createFromAsset(getAssets(), "fonts/Didot.ttc")
        mTypefaceBookItalic = Typeface.createFromAsset(getAssets(), "fonts/BodoniStd-BookItalic.otf")
    }

    val imageSize: Int
        get() = minOf(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)

    /**
     * Completely exit the app
     */
    fun exitApp() {
        exitProcess(0)
    }

    /**
     * Restart the app (needed after Tinker patching)
     */
    fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            17, // request code
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)
        exitApp()
    }

    fun playSound(@RawRes resId: Int) {
        if (soundPool == null) {
            soundPool = SoundPool.Builder().build()
        }
        val soundId = soundPool!!.load(this, resId, 1)
        soundPool!!.setOnLoadCompleteListener { _, _, _ ->
            soundPool!!.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    var httpCache: Cache? = null
        get() = field

    val typefaceMonth: Typeface
        get() {
            return mTypefaceIsabella
        }

    val typefaceDetail: Typeface
        get() {
            return mTypefaceRadam
        }

    val typefaceTitle: Typeface
        get() {
            return mTypefaceFangzheng
        }

    val typefaceDay: Typeface
        get() {
            return mTypefaceDidot
        }

    val typefaceSubtitle: Typeface
        get() {
            return mTypefaceBookItalic
        }

    companion object {
        private var sApp: App? = null

        @kotlin.jvm.JvmStatic
        fun get(): App? {
            return sApp
        }

        @kotlin.jvm.JvmField
        var sParseDateFormat: SimpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        @kotlin.jvm.JvmField
        var sMonthFormat: SimpleDateFormat = SimpleDateFormat("MMM", Locale.getDefault())
        @kotlin.jvm.JvmField
        var sWeekFormat: SimpleDateFormat = SimpleDateFormat("EEE", Locale.getDefault())
        @kotlin.jvm.JvmField
        var sDayFormat: SimpleDateFormat = SimpleDateFormat("d", Locale.getDefault())

        @kotlin.jvm.JvmField
        var sSolarFormat: SimpleDateFormat = SimpleDateFormat("yyyy年MMMd日 EEE", Locale.getDefault())
    }
}