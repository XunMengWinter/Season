package top.wefor.season.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.DisplayCutout;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.palette.graphics.Palette;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import top.wefor.season.App;
import top.wefor.season.R;
import top.wefor.season.data.ItemWatcher;
import top.wefor.season.data.http.BaseObserver;
import top.wefor.season.data.model.DateCardEntity;
import top.wefor.season.data.model.MusicBean;
import top.wefor.season.databinding.ActivityDetailBinding;
import top.wefor.season.databinding.LayoutDetailContentBinding;
import top.wefor.season.util.AnimationUtil;
import top.wefor.season.util.BitmapUtil;
import top.wefor.season.util.DateUtil;
import top.wefor.season.util.ShareUtil;
import top.wefor.season.util.SimpleMusicNotification;
import top.wefor.season.util.StringUtil;
import top.wefor.season.util.Toaster;
import top.wefor.season.widget.ShareKit;


/**
 * Created on 2018/12/22.
 *
 * @author ice
 */
public class DetailActivity extends BaseActivity {

    private static final String EXTRA_ENTITY = "extra_entity";
    private static final String MUSIC_BEAN = "music_bean";
    public static final String TRANSIT_VIEW = "transit_view";

    private LayoutDetailContentBinding binding;

    private MusicBean mMusicBean;
    private MediaPlayer mMediaPlayer;

    private boolean mIsShowMusic = false;
    private boolean mIsRotationMusic = false;
    private SimpleMusicNotification musicNotification;
    private BroadcastReceiver musicReceiver;

    public static void go(Context context, DateCardEntity dateCardEntity, @Nullable MusicBean musicBean, View transitionView) {
        ItemWatcher.get().watch(dateCardEntity);
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_ENTITY, JSON.toJSONString(dateCardEntity));
        if (musicBean != null) {
            Logger.i("go with musicBean");
            intent.putExtra(MUSIC_BEAN, JSON.toJSONString(musicBean));
        }
        Logger.i("go detail");
        //元素共享动画
        ActivityOptions optionsCompat
                = ActivityOptions.makeSceneTransitionAnimation(
                (Activity) context, transitionView, TRANSIT_VIEW);
        try {
            context.startActivity(intent, optionsCompat.toBundle());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            context.startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailBinding rootBinding = ActivityDetailBinding.inflate(getLayoutInflater());
        View rootView = rootBinding.getRoot();
        setContentView(rootView);
        binding = rootBinding.contentLayout;
        binding.titleTv.setTypeface(App.get().getTypefaceTitle());
        binding.detailTv.setTypeface(App.get().getTypefaceDetail());
        binding.monthTv.setTypeface(App.get().getTypefaceMonth());
        binding.dateTv.setTypeface(App.get().getTypefaceDay());
        binding.subtitleTv.setTypeface(App.get().getTypefaceSubtitle());
        binding.backIv.setOnClickListener(view -> onBackPressed());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            /*适配刘海屏*/
            getWindow().getDecorView().setOnApplyWindowInsetsListener((v, insets) -> {
                DisplayCutout displayCutout = insets.getDisplayCutout();
                if (displayCutout != null) {
                    int safeTop = displayCutout.getSafeInsetTop();
                    if (safeTop > 0) {
                        ViewGroup.LayoutParams layoutParams = binding.space.getLayoutParams();
                        layoutParams.height = safeTop;
                        binding.space.setLayoutParams(layoutParams);
                    }
                }
                return insets;
            });
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

        String musicStr = getIntent().getStringExtra(MUSIC_BEAN);
        if (musicStr != null) {
            // 请求存储权限并获取音乐文件
            new RxPermissions(this)
                    .request(
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                                    ? Manifest.permission.READ_MEDIA_AUDIO // Android 13+ 适配新权限
                                    : Manifest.permission.READ_EXTERNAL_STORAGE // Android 12 及以下
                    )
                    .subscribe(new BaseObserver<Boolean>(getLifecycle()) {
                        @Override
                        protected void onSucceed(Boolean result) {
                            if (result) {
                                binding.cdIv.postDelayed(()-> initMusic(musicStr), 500);
                            } else {
//                                Toaster.get().showToast("读取音乐失败");
                            }
                        }
                    });
        }

        if (mDateCardEntity.stable) {
            binding.editLockIv.setVisibility(View.GONE);
        }

        binding.monthTv.setText(mDateCardEntity.getMontStr());
        binding.dateTv.setText(mDateCardEntity.day);

        binding.titleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, StringUtil.getTitleSizeInPx(mDateCardEntity.title));
        binding.titleTv.setText(mDateCardEntity.title);
        binding.subtitleTv.setText(mDateCardEntity.subtitle);
        binding.detailTv.setText(mDateCardEntity.desc);

        Date date = mDateCardEntity.getDate();
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        String solarStr = dateFormat.format(date); // 格式化日期
        String starStr = DateUtil.getConstellation(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String lunarStr = mDateCardEntity.lunar;
        String showStr = "\n" + solarStr + " \t" + starStr + " \t" + lunarStr + " \t" + mDateCardEntity.solarTerm + "\n";

        binding.fullDateTv.setText(showStr);

        Glide.with(DetailActivity.this)
                .load(TextUtils.isEmpty(mDateCardEntity.imageUrl) ? mDateCardEntity.imageRes : mDateCardEntity.imageUrl)
                .apply(new RequestOptions().override(Objects.requireNonNull(App.get()).getImageSize()))
                .into(new DrawableImageViewTarget(binding.bigIv));

        updateColor(mDateCardEntity);

        binding.cardLayout.setOnLongClickListener(view -> {
            binding.bigIv.setFocusable(true);
            binding.bigIv.requestFocus();
            new ShareKit(DetailActivity.this)
                    .setOnShareItemClickListener(new ShareKit.OnShareItemClickListener() {
                        @Override
                        public void onWechatClick() {
                            ShareUtil.share(DetailActivity.this, BitmapUtil.loadBitmapFromView(binding.cardLayout), ShareUtil.TYPE_WECHAT);
                        }

                        @Override
                        public void onMomentClick() {
                            ShareUtil.share(DetailActivity.this, BitmapUtil.loadBitmapFromView(binding.cardLayout), ShareUtil.TYPE_WECHAT_TIMELINE);
                        }

                        @Override
                        public void onSaveClick() {
                            ShareUtil.share(DetailActivity.this, BitmapUtil.loadBitmapFromView(binding.cardLayout), ShareUtil.TYPE_GALLERY);
                        }
                    }).show(mLastTouchPoint.x, mLastTouchPoint.y);
            return true;
        });

        binding.textAreaView.setOnLongClickListener(view -> binding.cardLayout.performLongClick());

        binding.editLockIv.setOnClickListener(view -> {
            mCanEdit = !mCanEdit;
            updateEdit();
        });

        binding.fullDateTv.setOnClickListener(view -> {
            openCalendarToDate(mDateCardEntity.getDate().getTime());
        });

        binding.diyIv.setOnClickListener(view -> DIYActivity.go(DetailActivity.this, mDateCardEntity, binding.bigIv));

        binding.textAreaView.setOnClickListener(view -> {
            if (!mCanEdit)
                AnimationUtil.shake(binding.editLockIv);
        });

        //元素共享动画
        binding.bigIv.setTransitionName(TRANSIT_VIEW);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestMusicNotify() {
        new RxPermissions(this)
                .request(Manifest.permission.POST_NOTIFICATIONS)
                .subscribe(new BaseObserver<>(getLifecycle()) {
                    @Override
                    protected void onSucceed(Boolean result) {
                        if (result) {
                            initMusicNotification();
                        }
                    }
                });
    }


    private void initMusic(String musicStr) {
        Logger.i("show musicStr" + musicStr);
        if (isFinishing()){
            return;
        }

        mMusicBean = JSON.parseObject(musicStr, MusicBean.class);
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.reset(); //重置多媒体
            //指定音频文件地址
            mMediaPlayer.setDataSource(mMusicBean.filePath);
            //准备播放
            mMediaPlayer.prepare();
            mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                mIsRotationMusic = false;
                binding.cdIv.animate().cancel();
                binding.cdIv.clearAnimation();
                try {
                    mMediaPlayer.seekTo(0);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            });

            binding.cdIv.setVisibility(View.VISIBLE);
            binding.cdIv.setOnClickListener(view -> {
                if (mIsRotationMusic) {
                    pauseMusic();
                } else {
                    playMusic();
                }
            });
            binding.cdIv.setOnLongClickListener(view -> {
                changeContent();
                return true;
            });
            binding.cdIv.performClick();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestMusicNotify();
            } else {
                initMusicNotification();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e.getMessage());
        }
    }

    private void openCalendarToDate(long timestamp) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(CalendarContract.CONTENT_URI.buildUpon().appendPath("time").build());
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, timestamp);
        startActivity(intent);
    }

    private void changeContent() {
        mIsShowMusic = !mIsShowMusic;
        binding.bigIv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (mIsShowMusic) {
            binding.titleTv.setText(mMusicBean.title);
            binding.subtitleTv.setText("");
            binding.detailTv.setText("🎤 " + mMusicBean.artist + "\n\n");
            if (!TextUtils.isEmpty(mMusicBean.imageFilePath)) {
                Glide.with(this)
                        .load(new File(mMusicBean.imageFilePath))
                        .into(new CustomViewTarget<ImageView, Drawable>(binding.bigIv) {

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {

                            }

                            @Override
                            protected void onResourceCleared(@Nullable Drawable placeholder) {

                            }

                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition transition) {
                                binding.bigIv.setImageDrawable(resource);
                            }
                        });
            }
        } else {
            binding.titleTv.setText(mDateCardEntity.title);
            binding.subtitleTv.setText(mDateCardEntity.subtitle);
            binding.detailTv.setText(mDateCardEntity.desc);
            Glide.with(this)
                    .load(mDateCardEntity.imageRes != 0 ? mDateCardEntity.imageRes : mDateCardEntity.imageUrl)
                    .apply(new RequestOptions().override(App.get().getImageSize()))
                    .into(new DrawableImageViewTarget(binding.bigIv));
        }
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

    private void prise() {
        binding.priseIv.animate().cancel();
        binding.priseIv.clearAnimation();
        binding.priseIv.setAlpha(0.5f);
        binding.priseIv.setTranslationY(0f);
        binding.priseIv.setVisibility(View.VISIBLE);
        binding.priseIv.animate().translationY(-binding.priseIv.getHeight() * 2)
                .alpha(0f)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(1600)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        binding.priseIv.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private Palette mPalette;

    private boolean mCanEdit;

    private void updateEdit() {
        if (mCanEdit) {
            binding.editLockIv.setImageResource(R.drawable.ic_lock_open);
            binding.titleTv.setEnabled(true);
            binding.subtitleTv.setEnabled(true);
            binding.detailTv.setEnabled(true);
            binding.detailTv.requestFocus();
            binding.textAreaView.setVisibility(View.GONE);
        } else {
            binding.editLockIv.setImageResource(R.drawable.ic_lock);
            binding.titleTv.setEnabled(false);
            binding.subtitleTv.setEnabled(false);
            binding.detailTv.setEnabled(false);
            binding.textAreaView.setVisibility(View.VISIBLE);
            prise();
            mDateCardEntity.title = binding.titleTv.getText().toString();
            mDateCardEntity.subtitle = binding.subtitleTv.getText().toString();
            mDateCardEntity.desc = binding.detailTv.getText().toString();
            ItemWatcher.get().set(mDateCardEntity);
        }
    }

    private void showCdAnimation() {
        binding.cdIv.animate().rotation(360 * 6 * 60)
                .setDuration(10_000 * 6 * 60)
                .setInterpolator(new LinearInterpolator())
                .start();
    }

    @Override
    protected void onSofKeyboardClose() {
        super.onSofKeyboardClose();
        binding.cardLayout.requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsRotationMusic)
            showCdAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AnimationUtil.clearAnim(binding.priseIv);
        AnimationUtil.clearAnim(binding.cdIv);
    }

    private void updateNotification(boolean isPlaying) {
        if (musicNotification != null) {
            musicNotification.showNotification(mMusicBean.title, mMusicBean.artist, isPlaying);
        }
    }

    private void initMusicNotification() {
        musicNotification = new SimpleMusicNotification(DetailActivity.this);
        updateNotification(true);

        musicReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String action = intent.getAction();
                    String actionType = intent.getStringExtra("ACTION_TYPE");
                    Logger.i("onReceive " + action + " extra" + actionType);
                    if ("ACTION_PLAY".equals(actionType)) {
                        playMusic();
                    } else if ("ACTION_PAUSE".equals(actionType)) {
                        pauseMusic();
                    }
                }
            }
        };

        // 注册本地广播接收器
        IntentFilter filter = new IntentFilter("ACTION_MUSIC_CONTROL");
        LocalBroadcastManager.getInstance(this).registerReceiver(musicReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放 MediaPlayer
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;  // 释放引用
        }

        // 移除 Activity 过渡动画
        AnimationUtil.removeActivityFromTransitionManager(this);

        // 取消通知
        if (musicNotification != null) {
            musicNotification.cancelNotification();
            musicNotification = null; // 防止再次使用
        }

        // 取消注册广播接收器
        if (musicReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(musicReceiver);
            musicReceiver = null;  // 防止再次使用
        }
    }

    private static String TAG = "music";

    // 播放音乐
    private void playMusic() {
        if (mMediaPlayer == null || mMediaPlayer.isPlaying()) {
            return;
        }
        if (!mMediaPlayer.isPlaying()) {
            mIsRotationMusic = true;
            showCdAnimation();
            mMediaPlayer.start();
            updateNotification(true);
            Log.d(TAG, "Music playing");
        }
    }

    // 暂停音乐
    private void pauseMusic() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mIsRotationMusic = false;
            AnimationUtil.clearAnim(binding.cdIv);
            mMediaPlayer.pause();
            updateNotification(false);
            Log.d(TAG, "Music paused");
        }
    }

    private DateCardEntity mDateCardEntity;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mLastTouchPoint.x = (int) ev.getRawX();
        mLastTouchPoint.y = (int) ev.getRawY();
        return super.dispatchTouchEvent(ev);
    }

    private Point mLastTouchPoint = new Point();
}
