package top.wefor.season.widget.page

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.orhanobut.logger.Logger

/**
 * PageBehavior handles scroll behavior for switching pages
 */
class PageBehavior(context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<View>(context, attrs) {

    var mOnPageChanged: OnPageChanged? = null

    private var scrollY = 0 // 跟踪滚动位置

    fun setOnPageChanged(onPageChanged: OnPageChanged?) {
        mOnPageChanged = onPageChanged
    }

    /**
     * Set the scroll Y value
     */
    fun setScrollY(scrollY: Int) {
        this.scrollY = scrollY
    }

    /**
     * Scroll the page back to the top
     */
    fun backToTop(view: View) {
        if (view is ScrollView) {
            view.smoothScrollTo(0, 0) // 平滑滚动到顶部
            mOnPageChanged?.toTop()
        }
    }

    /**
     * Scroll the page to the bottom
     */
    fun scrollToBottom(view: View) {
        if (view is ScrollView) {
            view.post {
                view.smoothScrollTo(0, view.bottom) // 平滑滚动到底部
                mOnPageChanged?.toBottom()
            }
        }
    }

    /**
     * Respond to nested scroll events, track scroll position
     */
    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View,
        target: View, axes: Int, type: Int
    ): Boolean {
        // Only handle vertical scroll events
        return axes == View.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout, child: View, target: View,
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int
    ) {
        scrollY += dyConsumed
        if (scrollY <= 0) {
            mOnPageChanged?.toTop()
        } else if (scrollY >= child.height) {
            mOnPageChanged?.toBottom()
            Logger.i("toBottom")
        }
    }

    interface OnPageChanged {
        fun toTop() // Called when the page is scrolled to the top
        fun toBottom() // Called when the page is scrolled to the bottom
    }
}