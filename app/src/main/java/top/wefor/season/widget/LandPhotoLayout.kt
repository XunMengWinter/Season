package top.wefor.season.widget

import android.content.Context
import android.util.AttributeSet

/**
 * Created on 2018/12/20.
 *
 * @author ice
 */
class LandPhotoLayout : PhotoLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int w = MeasureSpec.getSize(widthMeasureSpec);
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val w = h * 2 / 3
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY)
        )
    }
}
