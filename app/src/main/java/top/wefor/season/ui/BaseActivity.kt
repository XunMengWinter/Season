package top.wefor.season.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import top.wefor.season.R
import top.wefor.season.util.ScreenUtil

abstract class BaseActivity : FragmentActivity(), OnGlobalLayoutListener {
    private var mRootView: View? = null
    private var isSoftKeyboardOpened = false
    private var dp100px = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dp100px = resources.getDimensionPixelSize(R.dimen.mini_keyboard_height)
        hideSystemUI()
    }



    // Directly use the lifecycle from FragmentActivity
    override val lifecycle: Lifecycle
        get() = super.lifecycle


    protected fun hideSystemUI() {
        ScreenUtil.hideSystemUI(window)
        mRootView = window.decorView
        if (mRootView != null) mRootView!!.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    protected fun windowFocus(hasFocus: Boolean) {
        if (hasFocus) ScreenUtil.hideSystemUI(window)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        windowFocus(hasFocus)
    }


    override fun onGlobalLayout() {
        if (mRootView == null) return

        /* 输入框收起后隐藏导航栏 */
        val r = Rect()
        //r will be populated with the coordinates of your view that area still visible.
        mRootView!!.getWindowVisibleDisplayFrame(r)

        val heightDiff = mRootView!!.rootView.height - (r.bottom - r.top)
        if (!isSoftKeyboardOpened && heightDiff > dp100px) { // if more than 100 pixels, its probably a keyboard...
            isSoftKeyboardOpened = true
            onSofKeyboardOpen()
        } else if (isSoftKeyboardOpened && heightDiff < dp100px) {
            isSoftKeyboardOpened = false
            onSofKeyboardClose()
        }
    }

    protected fun onSofKeyboardOpen() {
        // open
    }

    protected open fun onSofKeyboardClose() {
        // close
        ScreenUtil.hideSystemUI(window)
    }

}
