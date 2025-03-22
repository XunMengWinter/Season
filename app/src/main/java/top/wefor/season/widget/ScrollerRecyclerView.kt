package top.wefor.season.widget

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import top.wefor.season.ui.MainActivity
import kotlin.math.max

/**
 * Created on 2018/12/20.
 *
 * @author ice
 */
class ScrollerRecyclerView : RecyclerView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private var mIsUserScroll = false
    private var mIsScrollToEnd = false
    private val scroller = CustomSmoothScroller(context, customSpeed = 16f)
    private val linearLayoutManager: LinearLayoutManager?
        get() = layoutManager as? LinearLayoutManager

    override fun stopNestedScroll(type: Int) {
        super.stopNestedScroll(type)
        //type==1 自滚动停止（包括惯性）。
        //type==0 手动按压停止。
//        Logger.i("stopNestedScroll " + type);
        if (type == 0) {
            mIsUserScroll = true
        }
        if (type == 1 && mIsUserScroll) {
            mIsUserScroll = false
            innerScroll()
        }
    }

    override fun onScrolled(dx: Int, dy: Int) {
        val isVertical = linearLayoutManager?.orientation == VERTICAL
        mIsScrollToEnd = if (isVertical) dy > 0 else dx > 0
        super.onScrolled(dx, dy)
    }

    private fun innerScroll() {
        // 滚动方向
        val needScrollToEnd = mIsScrollToEnd
        scroller.updateSpeed(if (MainActivity.isLand) 24f else 16f)
        val linearLayoutManager = this.linearLayoutManager ?: return
        val pos = if (needScrollToEnd) {
            linearLayoutManager.findLastCompletelyVisibleItemPosition() + 7
        } else {
            linearLayoutManager.findFirstCompletelyVisibleItemPosition() - 7
        }
        scroller.targetPosition = max(pos, 0)
        linearLayoutManager.startSmoothScroll(scroller)
    }

    fun scrollToPositionSlow(position: Int, perPixMs: Float) {
        scroller.updateSpeed(if (MainActivity.isLand) perPixMs * 2 else perPixMs)
        scroller.targetPosition = position
        linearLayoutManager?.startSmoothScroll(scroller)
    }

    class CustomSmoothScroller(context: Context, private var customSpeed: Float) :
        LinearSmoothScroller(context) {

        fun updateSpeed(speed: Float) {
            customSpeed = speed
        }

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return customSpeed
        }
    }


}
