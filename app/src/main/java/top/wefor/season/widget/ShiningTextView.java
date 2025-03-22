package top.wefor.season.widget;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;
import android.util.AttributeSet;
import android.util.Property;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import top.wefor.season.R;

/**
 * 闪闪发光的TextView。
 * <p>
 * Created on 2018/3/2.
 *
 * @author ice
 */

public class ShiningTextView extends AppCompatTextView {

    private ObjectAnimator mObjectAnimator;

    public ShiningTextView(Context context) {
        super(context);
    }

    public ShiningTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShiningTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*闪亮✨吧*/
    public void startShining() {
        TextView textView = this;
        String text = textView.getText().toString();
        AnimatedColorSpan span = new AnimatedColorSpan(getContext());
        final SpannableString spannableString = new SpannableString(text);
        int start = 0;
        int end = text.length();
        spannableString.setSpan(span, start, end, 0);

        mObjectAnimator = ObjectAnimator.ofFloat(
                span, ANIMATED_COLOR_SPAN_FLOAT_PROPERTY, 0, 100);
        mObjectAnimator.setEvaluator(new FloatEvaluator());
        mObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(spannableString);
            }
        });
        mObjectAnimator.setInterpolator(new LinearInterpolator());
        mObjectAnimator.setDuration(DateUtils.SECOND_IN_MILLIS);
        mObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mObjectAnimator.start();
    }

    /*停止闪亮*/
    public void stopShining() {
        if (mObjectAnimator != null) {
            mObjectAnimator.cancel();
        }
    }

    private static final Property<AnimatedColorSpan, Float> ANIMATED_COLOR_SPAN_FLOAT_PROPERTY
            = new Property<AnimatedColorSpan, Float>(Float.class, "ANIMATED_COLOR_SPAN_FLOAT_PROPERTY") {
        @Override
        public void set(AnimatedColorSpan span, Float value) {
            span.setTranslateXPercentage(value);
        }

        @Override
        public Float get(AnimatedColorSpan span) {
            return span.getTranslateXPercentage();
        }
    };

    private static class AnimatedColorSpan extends CharacterStyle implements UpdateAppearance {
        private final int[] colors;
        private Shader shader = null;
        private Matrix matrix = new Matrix();
        private float translateXPercentage = 0;

        public AnimatedColorSpan(Context context) {
            colors = context.getResources().getIntArray(R.array.rainbow);
        }

        public void setTranslateXPercentage(float percentage) {
            translateXPercentage = percentage;
        }

        public float getTranslateXPercentage() {
            return translateXPercentage;
        }

        @Override
        public void updateDrawState(TextPaint paint) {
            paint.setStyle(Paint.Style.FILL);
            float width = paint.getTextSize() * colors.length;
            if (shader == null) {
                shader = new LinearGradient(0, 0, 0, width, colors, null,
                        Shader.TileMode.MIRROR);
            }
            matrix.reset();
            matrix.setRotate(90);
            matrix.postTranslate(width * translateXPercentage, 0);
            shader.setLocalMatrix(matrix);
            paint.setShader(shader);
        }
    }
}
