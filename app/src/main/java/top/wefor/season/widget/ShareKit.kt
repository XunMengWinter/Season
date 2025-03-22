package top.wefor.season.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import top.wefor.circularanim.CircularAnim
import top.wefor.season.R
import top.wefor.season.databinding.LayoutShareBinding
import java.util.Random
import kotlin.math.sqrt

/**
 * Created on 2018/12/21.
 *
 * @author ice
 */
class ShareKit(private var mActivity: Activity?) {
    private val binding: LayoutShareBinding
    private var mView: View?
    private var mSize = 0

    interface OnShareItemClickListener {
        fun onWechatClick()

        fun onMomentClick()

        fun onSaveClick()
    }

    interface onApiClickListener {
        fun onApiClick()
    }

    var mOnShareItemClickListener: OnShareItemClickListener? = null
    var mOnApiClickListener: onApiClickListener? = null

    fun setOnShareItemClickListener(onShareItemClickListener: OnShareItemClickListener?): ShareKit {
        mOnShareItemClickListener = onShareItemClickListener
        return this
    }

    fun setOnApiClickListener(onApiClickListener: onApiClickListener?) {
        mOnApiClickListener = onApiClickListener
    }

    fun show(x: Int, y: Int) {
        mTriggerPoint = Point(x, y)
        binding.backLayout.setOnClickListener { view: View? -> dismiss() }

        if (mOnShareItemClickListener != null) {
            binding.wechatIv.setOnClickListener { v: View? ->
                mOnShareItemClickListener!!.onWechatClick()
                dismiss()
            }
            binding.momentIv.setOnClickListener { v: View? ->
                mOnShareItemClickListener!!.onMomentClick()
                dismiss()
            }
            binding.saveIv.setOnClickListener { v: View? ->
                mOnShareItemClickListener!!.onSaveClick()
                dismiss()
            }
        }

        if (mOnApiClickListener != null) {
            if (Random().nextInt(365) < 7) {
                binding.apiIv.alpha = 0.32f
            }
            binding.apiIv.setOnClickListener { view: View? -> mOnApiClickListener!!.onApiClick() }
        }

        binding.wechatIv.post {
            CircularAnim.show(binding.backLayout).triggerPoint(mTriggerPoint).duration(500).go()
            mSize = binding.wechatIv.height
            resetView(binding.wechatIv, x, y)
            resetView(binding.momentIv, x, y)
            resetView(binding.saveIv, x, y)
            binding.wechatIv.animate().translationYBy(-mSize * 3.5f).scaleX(1f).scaleY(1f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        binding.wechatIv.animate().setListener(null)
                        if (mRandom.nextDouble() > 0.8) randomMove(binding.wechatIv)
                    }
                })
                .setInterpolator(OvershootInterpolator()).setDuration(500).start()

            binding.momentIv.animate().translationYBy(-mSize * 2.5f).scaleX(1f).scaleY(1f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        binding.momentIv.animate().setListener(null)
                        if (mRandom.nextBoolean()) revolution(binding.momentIv, binding.saveIv)
                    }
                })
                .setInterpolator(OvershootInterpolator()).setDuration(400).start()
            binding.saveIv.animate().translationYBy(-mSize * 1.5f).scaleX(1f).scaleY(1f)
                .setInterpolator(OvershootInterpolator()).setDuration(320).start()
        }
    }

    private fun revolution(view: View, triggerView: View) {
        val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.ABSOLUTE, triggerView.x + triggerView.width / 2,
            Animation.ABSOLUTE, triggerView.y + triggerView.height / 2
        )
        rotateAnimation.duration = 60000
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.repeatCount = 365
        view.startAnimation(rotateAnimation)
    }

    private fun randomMove(view: View) {
        view.postDelayed({
            var x = view.x
            var y = view.y
            val dx = mRandom.nextInt(101) - 50
            var dy = sqrt((mRandom.nextInt(2501) + 1250 - dx * dx).toDouble()).toInt()
            if (mRandom.nextBoolean()) dy = -dy
            x = x + dx
            y = y + dy
            view.animate().translationY(y).translationX(x).setInterpolator(LinearInterpolator())
                .setDuration(1000).start()
            randomMove(view)
        }, (1000 + mRandom.nextInt(1001)).toLong())
    }

    private var mTriggerPoint: Point? = null
    private val mRandom = Random()

    private fun resetView(view: View, x: Int, y: Int) {
        view.x = (x - mSize / 2).toFloat()
        view.y = (y - mSize / 2).toFloat()
        view.scaleX = 0.3f
        view.scaleY = 0.3f
        view.visibility = View.VISIBLE
    }

    private var mIsDismissing = false

    init {
        mView = mActivity!!.layoutInflater.inflate(R.layout.layout_share, null)
        (mActivity!!.window.decorView as ViewGroup).addView(mView)
        binding = LayoutShareBinding.bind(mView!!)

        //        mBackLayout.setAlpha(0f);
        binding.backLayout.visibility = View.INVISIBLE
        binding.wechatIv.visibility = View.INVISIBLE
        binding.momentIv.visibility = View.INVISIBLE
        binding.saveIv.visibility = View.INVISIBLE
    }

    private fun dismiss() {
        if (mIsDismissing) return
        mIsDismissing = true
        if (mView != null && mActivity != null) {
            binding.backLayout.animate().alpha(0f).setDuration(320)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        (mActivity!!.window.decorView as ViewGroup).removeView(mView)
                        mView = null
                        mActivity = null
                    }
                })
                .start()
            binding.wechatIv.animate().translationYBy(mSize * 1.7f).scaleX(0.5f).scaleY(0.5f)
                .setDuration(300).start()
            binding.momentIv.animate().translationYBy(mSize * 1.2f).scaleX(0.5f).scaleY(0.5f)
                .setDuration(300).start()
            binding.saveIv.animate().translationYBy(mSize * 0.7f).scaleX(0.5f).scaleY(0.5f)
                .setDuration(300).start()
        }
    }
}
