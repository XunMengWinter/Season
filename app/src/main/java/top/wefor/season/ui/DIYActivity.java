package top.wefor.season.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.DisplayCutout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.orhanobut.logger.Logger;

import top.wefor.season.App;
import top.wefor.season.R;
import top.wefor.season.data.model.DateCardEntity;
import top.wefor.season.databinding.ActivityDiyBinding;
import top.wefor.season.util.AnimationUtil;
import top.wefor.season.util.BitmapUtil;
import top.wefor.season.util.ImageUtil;
import top.wefor.season.util.PrefHelper;
import top.wefor.season.util.QRCodeUtil;
import top.wefor.season.util.ShareUtil;
import top.wefor.season.util.StringUtil;
import top.wefor.season.widget.ShareKit;

/**
 * Created on 2018/12/23.
 *
 * @author ice
 */
public class DIYActivity extends BaseActivity {

    private static final String EXTRA_ENTITY = "extra_entity";
    public static final String TRANSIT_VIEW = "transit_view";

    private ActivityDiyBinding binding;

    public static void go(Context context, DateCardEntity dateCardEntity, View transitionView) {
        Intent intent = new Intent(context, DIYActivity.class);
        intent.putExtra(EXTRA_ENTITY, JSON.toJSONString(dateCardEntity));

        //元素共享动画
        ActivityOptionsCompat optionsCompat
                = ActivityOptionsCompat.makeSceneTransitionAnimation(
                (Activity) context, transitionView, TRANSIT_VIEW);
        try {
            ActivityCompat.startActivity(context, intent,
                    optionsCompat.toBundle());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            context.startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiyBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);

        binding.titleTv.setTypeface(App.get().getTypefaceTitle());
        binding.detailTv.setTypeface(App.get().getTypefaceDetail());
        binding.monthTv.setTypeface(App.get().getTypefaceMonth());
        binding.dateTv.setTypeface(App.get().getTypefaceDay());
        binding.subtitleTv.setTypeface(App.get().getTypefaceSubtitle());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            /*适配刘海屏*/
            try {
                DisplayCutout displayCutout = getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
                if (displayCutout != null) {
                    int safeTop = displayCutout.getSafeInsetTop();
                    if (safeTop > 0) {
                        ViewGroup.LayoutParams layoutParams = binding.space.getLayoutParams();
                        layoutParams.height = safeTop;
                        binding.space.setLayoutParams(layoutParams);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            String imageEntityStr = getIntent().getStringExtra(EXTRA_ENTITY);
            mDateCardEntity = JSON.parseObject(imageEntityStr, DateCardEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mDateCardEntity == null) {
            return;
        }

        binding.monthTv.setText(mDateCardEntity.getMontStr());
        binding.dateTv.setText(mDateCardEntity.day);
        binding.titleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, StringUtil.getTitleSizeInPx(mDateCardEntity.title));
        binding.titleTv.setText(mDateCardEntity.title);
        binding.subtitleTv.setText(mDateCardEntity.subtitle);
        binding.detailTv.setText(mDateCardEntity.desc);

        int padding = PrefHelper.get().getDiyPadding();
        if (padding > 0) {
            binding.cardLayout.setPadding(padding, padding, padding, padding);
            binding.paddingSeekBar.setProgress(padding);
        }

        Glide.with(DIYActivity.this)
                .load(TextUtils.isEmpty(mDateCardEntity.imageUrl) ? mDateCardEntity.imageRes : mDateCardEntity.imageUrl)
                .apply(new RequestOptions().override(App.get().getImageSize()))
                .into(new DrawableImageViewTarget(binding.bigIv));

        updateColor(mDateCardEntity);

        binding.qrcodeIv.post(() -> {
            int qrWhite = 0x00000000;
            int qrBlack = 0x80123456;
            int qrSize = Math.min(binding.qrcodeIv.getWidth(), binding.qrcodeIv.getHeight());
//            Bitmap qrCodeBitmap = QRCodeUtil.createQRCodeBitmap("Winter is coming", qrSize, qrSize, "UTF-8", "H", "0", qrBlack, qrWhite);
//            binding.qrcodeIv.setImageBitmap(qrCodeBitmap);
            binding.qrcodeIv.setImageResource(R.drawable.img_qr_season);
            mQrCodeShow = PrefHelper.get().getDiyQrCodeShow();
            updateQrCodeShow();
        });

        binding.backIv.setOnClickListener(view -> onBackPressed());

        binding.shareIv.setOnClickListener(view -> new ShareKit(DIYActivity.this)
                .setOnShareItemClickListener(new ShareKit.OnShareItemClickListener() {
                    @Override
                    public void onWechatClick() {
                        ShareUtil.share(DIYActivity.this, BitmapUtil.loadBitmapFromView(binding.cardLayout), ShareUtil.TYPE_WECHAT);
                    }

                    @Override
                    public void onMomentClick() {
                        ShareUtil.share(DIYActivity.this, BitmapUtil.loadBitmapFromView(binding.cardLayout), ShareUtil.TYPE_WECHAT_TIMELINE);
                    }

                    @Override
                    public void onSaveClick() {
                        ShareUtil.share(DIYActivity.this, BitmapUtil.loadBitmapFromView(binding.cardLayout), ShareUtil.TYPE_GALLERY);
                    }
                })
                .show(mLastTouchPoint.x, mLastTouchPoint.y));

        binding.qrcodeEditIv.setOnClickListener(view -> {
            mQrCodeShow = !mQrCodeShow;
            updateQrCodeShow();
        });

        binding.paddingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int padding = i;
                binding.cardLayout.setPadding(padding, padding, padding, padding);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.bigIv.setOnClickListener(view -> pickImage(REQUEST_IMAGE));
        binding.qrcodeIv.setOnClickListener(view -> pickImage(REQUEST_QRCODE));

        //元素共享动画
        ViewCompat.setTransitionName(binding.bigIv, TRANSIT_VIEW);
    }

    private void updateColor(DateCardEntity entity) {
        try {
            binding.dateTv.setTextColor(entity.mDayColor);
            binding.monthTv.setTextColor(entity.mMonthColor);
            binding.subtitleTv.setTextColor(entity.mSubtitleColor);
            binding.detailTv.setTextColor(entity.mDetailColor);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e.getMessage() + " updateColor");
        }
    }

    private void updateQrCodeShow() {
        if (mQrCodeShow) {
            binding.qrcodeEditIv.setImageResource(R.drawable.ic_qrcode);
            binding.qrcodeIv.setVisibility(View.VISIBLE);
        } else {
            binding.qrcodeIv.setVisibility(View.GONE);
            binding.qrcodeEditIv.setImageResource(R.drawable.ic_qrcode_disable);
        }
    }

    private DateCardEntity mDateCardEntity;
    private boolean mQrCodeShow = true;

    @Override
    protected void onSofKeyboardClose() {
        super.onSofKeyboardClose();
//        mDetailTv.clearFocus();
//        mSubtitleTv.clearFocus();
//        mTitleTv.clearFocus();
        binding.cardLayout.requestFocus();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mLastTouchPoint.x = (int) ev.getRawX();
        mLastTouchPoint.y = (int) ev.getRawY();
        return super.dispatchTouchEvent(ev);
    }

    private Point mLastTouchPoint = new Point();

    private static final int REQUEST_IMAGE = 17;
    private static final int REQUEST_QRCODE = 18;

    private void pickImage(int requestCode) {
        Intent intent = ImageUtil.pickImageIntent();
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            boolean isBigIv = requestCode == REQUEST_IMAGE;
            if (isBigIv || requestCode == REQUEST_QRCODE) {
                if (data != null) {
                    binding.bigIv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    ImageView imageView = isBigIv ? binding.bigIv : binding.qrcodeIv;
                    Glide.with(this)
                            .load(data.getData())
                            .into(new CustomViewTarget<ImageView, Drawable>(imageView) {

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {

                                }

                                @Override
                                protected void onResourceCleared(@Nullable Drawable placeholder) {

                                }

                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition transition) {
                                    imageView.setImageDrawable(resource);
                                }
                            });
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrefHelper.get().setDiyQrCodeShow(mQrCodeShow);
        PrefHelper.get().setDiyPadding(binding.paddingSeekBar.getProgress());
        AnimationUtil.removeActivityFromTransitionManager(this);
    }

}
