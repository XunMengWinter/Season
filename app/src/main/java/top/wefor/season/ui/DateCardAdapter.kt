package top.wefor.season.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.orhanobut.logger.Logger
import top.wefor.season.App
import top.wefor.season.R
import top.wefor.season.data.model.DateCardEntity
import top.wefor.season.databinding.ItemDateCardBinding
import top.wefor.season.util.StringUtil

/**
 * Created on 2018/12/21.
 *
 * @author ice
 */
class DateCardAdapter(private val mContext: Context, list: List<DateCardEntity>, isLand: Boolean) :
    RecyclerView.Adapter<DateCardAdapter.MyHolder>() {
    interface OnCardClickListener {
        fun onCardLongClick(position: Int, holder: MyHolder?)

        fun onCardClick(position: Int, holder: MyHolder?)

        fun onDateLongClick(position: Int, holder: MyHolder?)
    }

    private val mList: List<DateCardEntity> = list
    private val mIsLand = isLand
    private var mOnCardClickListener: OnCardClickListener? = null

    fun setOnCardClickListener(onCardClickListener: OnCardClickListener?) {
        mOnCardClickListener = onCardClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_date_card, null),
            mIsLand
        )
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val position = position % mList.size
        val entity: DateCardEntity = mList[position]
        holder.binding.monthTv.text = entity.montStr
        holder.binding.dateTv.text = entity.day
        holder.binding.titleTv.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            StringUtil.getTitleSizeInPx(entity.title)
        )
        holder.binding.titleTv.text = entity.title
        holder.binding.subtitleTv.text = entity.subtitle
        holder.binding.detailTv.text = entity.desc
        val hasLoaded = (entity.mGradientDrawable != null)
        if (hasLoaded) {
            holder.updateColor(entity)
        }
        Glide.with(mContext)
            .load(if (TextUtils.isEmpty(entity.imageUrl)) entity.imageRes else entity.imageUrl)
            .apply(RequestOptions().override(App.get()?.imageSize!!))
            .into(object : DrawableImageViewTarget(holder.binding.bigIv) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    super.onResourceReady(resource, transition)
                    if (hasLoaded) return
                    var bitmap: Bitmap? = null
                    if (resource is GifDrawable) {
                        bitmap = resource.getFirstFrame()
                    } else if (resource is BitmapDrawable) {
                        bitmap = resource.getBitmap()
                    }
                    if (bitmap == null) {
                        Logger.e("bitmap is null, url:" + entity.imageUrl + " res:" + entity.imageRes)
                        return
                    }

                    entity.mImageW = bitmap.width
                    entity.mImageH = bitmap.height

                    val palette: Palette = Palette.from(bitmap).maximumColorCount(16).generate()
                    val color: Int = palette.getDominantColor(0x000000) and 0x50ffffff
                    val colors = intArrayOf(0x0, color, 0x0)
                    val gradientDrawable: GradientDrawable = GradientDrawable()
                    if (mIsLand) gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT)
                    gradientDrawable.setColors(colors)

                    entity.mGradientDrawable = gradientDrawable
                    entity.mDayColor = palette.getMutedColor(-0x71564c) and -0x2f0f0f10
                    entity.mMonthColor =
                        palette.getLightMutedColor(entity.mDayColor) and -0x2f0f0f10
                    entity.mWeekColor =
                        palette.getLightVibrantColor(entity.mMonthColor) and -0x2f0f0f10
                    entity.mSubtitleColor = palette.getVibrantColor(-0xbbbbbc) and -0x5f000001
                    entity.mDetailColor = palette.getDarkVibrantColor(-0x3f888889)
                    holder.updateColor(entity)
                }
            })
        if (mOnCardClickListener != null) {
            holder.binding.cardLayout.setOnLongClickListener {
                mOnCardClickListener!!.onCardLongClick(position, holder)
                true
            }
            holder.binding.dateTv.setOnLongClickListener {
                mOnCardClickListener!!.onDateLongClick(position, holder)
                true
            }
            holder.binding.monthTv.setOnLongClickListener {
                mOnCardClickListener!!.onDateLongClick(position, holder)
                true
            }

            holder.binding.cardLayout.setOnClickListener {
                mOnCardClickListener!!.onCardClick(
                    position,
                    holder
                )
            }
            holder.binding.dateTv.setOnClickListener { view -> holder.binding.cardLayout.performClick() }
        }
    }

    override fun getItemCount(): Int {
//        return mList.size
        if (mList.isEmpty()) {
            return 0
        }
        return mList.size.coerceAtLeast(4 * 60 * 24 * 365)
    }

    class MyHolder internal constructor(rootView: View, var isLand: Boolean) :
        RecyclerView.ViewHolder(rootView) {
        @JvmField
        var binding: ItemDateCardBinding

        init {
            rootView.isFocusable = true
            binding = ItemDateCardBinding.bind(rootView)

            binding.titleTv.setTypeface(App.get()?.typefaceTitle)
            binding.dateTv.setTypeface(App.get()?.typefaceDay)
            binding.subtitleTv.setTypeface(App.get()?.typefaceSubtitle)
            binding.detailTv.setTypeface(App.get()?.typefaceDetail)
            binding.monthTv.setTypeface(App.get()?.typefaceMonth)
        }

        fun updateColor(entity: DateCardEntity) {
            try {
                binding.rootLayout.setBackground(entity.mGradientDrawable)
                binding.dateTv.setTextColor(entity.mDayColor)
                binding.monthTv.setTextColor(entity.mMonthColor)
                binding.subtitleTv.setTextColor(entity.mSubtitleColor)
                binding.detailTv.setTextColor(entity.mDetailColor)
            } catch (e: Exception) {
                e.printStackTrace()
                Logger.e(e.message + " updateColor")
            }
        }

        fun updateText(entity: DateCardEntity) {
            binding.titleTv.setText(entity.title)
            binding.subtitleTv.setText(entity.subtitle)
            binding.detailTv.setText(entity.desc)
        }
    }
}
