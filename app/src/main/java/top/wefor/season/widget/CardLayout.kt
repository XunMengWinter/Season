package top.wefor.season.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

/**
 * Created on 2018/12/20.
 *
 * @author ice
 */
class CardLayout : LinearLayout {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //        int w = MeasureSpec.getSize(widthMeasureSpec);
//        int h = w * 16 / 10;
//        super.onMeasure(
//                MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
//                MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY)
//        );
    }
}
