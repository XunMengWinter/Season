package top.wefor.season.ui;

import android.Manifest;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import top.wefor.season.App;
import top.wefor.season.R;
import top.wefor.season.data.http.BaseObserver;
import top.wefor.season.data.http.NowApi;
import top.wefor.season.data.model.BaseResult;
import top.wefor.season.data.model.DateCardEntity;
import top.wefor.season.data.model.MusicBean;
import top.wefor.season.databinding.ActivityMainBinding;
import top.wefor.season.util.BitmapUtil;
import top.wefor.season.util.CardGenerator;
import top.wefor.season.util.DateUtil;
import top.wefor.season.util.MusicUtil;
import top.wefor.season.util.PrefHelper;
import top.wefor.season.util.ShareUtil;
import top.wefor.season.widget.ShareKit;

public class MainActivity extends BaseActivity implements DateCardAdapter.OnCardClickListener {

    private static final String KEY_POSITION = "position";
    private static final String KEY_LIST_JSON = "list_json";

    private ActivityMainBinding binding;
    private DateCardAdapter mDateCardAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<DateCardEntity> mList = new ArrayList<>();

    public static boolean isLand;
    private final Random mRandom = new Random();
    private List<Integer> mPwdList = new ArrayList<>();
    private List<MusicBean> mMusicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);

        isLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        mDateCardAdapter = new DateCardAdapter(this, mList, isLand);
        mDateCardAdapter.setOnCardClickListener(this);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(mLayoutManager = new LinearLayoutManager(this, isLand ? RecyclerView.HORIZONTAL : RecyclerView.VERTICAL, false));
        binding.recyclerView.setAdapter(mDateCardAdapter);

        int position = 0;
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(KEY_POSITION);
            mList.clear();
            mList.addAll(JSON.parseArray(savedInstanceState.getString(KEY_LIST_JSON, "[]"), DateCardEntity.class));
        }

        if (mList.isEmpty()) {
            getData();
        } else {
            mDateCardAdapter.notifyDataSetChanged();
            binding.recyclerView.scrollToPosition(position);
        }

        openMusic();
    }

    private void getData() {
        NowApi.getSeason().getCalendar(PrefHelper.get().getCat2025())
                .map(listBaseResult -> {
                    final int dateOffset = listBaseResult.dateOffset;
                    if (dateOffset != 0) {
                        try {
                            for (DateCardEntity cardEntity : listBaseResult.data) {
                                Date date = App.sParseDateFormat.parse(cardEntity.datestamp + "");
                                date.setTime(date.getTime() + TimeUnit.DAYS.toMillis(dateOffset));
                                cardEntity.datestamp = Integer.parseInt(App.sParseDateFormat.format(date));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Logger.e(e.getMessage());
                        }
                    }
                    return listBaseResult;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResult<List<DateCardEntity>>>(getLifecycle()) {
                    @Override
                    protected void onSucceed(BaseResult<List<DateCardEntity>> result) {
                        mList.clear();
                        mList.add(CardGenerator.getSeason());
                        mList.addAll(result.data);
                        mDateCardAdapter.notifyDataSetChanged();
                        try {
                            final int lastPos = PrefHelper.get().getLastPosition();
                            binding.recyclerView.scrollToPosition(lastPos);
                            Calendar calendar = Calendar.getInstance();
                            int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
                            int yyMMdd = Integer.parseInt(App.sParseDateFormat.format(new Date()));
                            int pos = getXPos(dayOfYear, yyMMdd, mList);
                            final int diffPositive = Math.abs(pos - lastPos);
                            if (pos > 0 && diffPositive > 0) {
                                float rate = (float) Math.sqrt(diffPositive);
                                binding.recyclerView.scrollToPositionSlow(pos, 8 / rate);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Logger.e(e.getMessage());
                        }
                    }

                    @Override
                    protected void onFailed(@Nullable String msg) {
                        super.onFailed(msg);
                        if (mList.isEmpty()) {
                            mList.add(CardGenerator.getSeason());
                            mList.add(CardGenerator.getError(msg));
                            mDateCardAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private int getXPos(int about, int exact, @NonNull List<DateCardEntity> list) {
        for (int i = about; i < Math.min(about * 2, list.size()); i++) {
            if (list.get(i).datestamp == exact) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POSITION, mLayoutManager.findFirstVisibleItemPosition());
        outState.putString(KEY_LIST_JSON, JSON.toJSONString(mList));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrefHelper.get().setLastPosition(mLayoutManager.findFirstVisibleItemPosition());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mLastTouchPoint.x = (int) ev.getRawX();
        mLastTouchPoint.y = (int) ev.getRawY();
        return super.dispatchTouchEvent(ev);
    }

    private final Point mLastTouchPoint = new Point();

    @Override
    public void onCardLongClick(int position, DateCardAdapter.MyHolder holder) {
        ShareKit shareKit = new ShareKit(this)
                .setOnShareItemClickListener(new ShareKit.OnShareItemClickListener() {
                    @Override
                    public void onWechatClick() {
                        ShareUtil.share(MainActivity.this, BitmapUtil.loadBitmapFromView(holder.binding.cardLayout), ShareUtil.TYPE_WECHAT);
                    }

                    @Override
                    public void onMomentClick() {
                        ShareUtil.share(MainActivity.this, BitmapUtil.loadBitmapFromView(holder.binding.cardLayout), ShareUtil.TYPE_WECHAT_TIMELINE);
                    }

                    @Override
                    public void onSaveClick() {
                        ShareUtil.share(MainActivity.this, BitmapUtil.loadBitmapFromView(holder.binding.cardLayout), ShareUtil.TYPE_GALLERY);
                    }
                });
        shareKit.setOnApiClickListener(() -> {
            AppCompatEditText editText = new AppCompatEditText(this);
            editText.setHint("api name:");
            editText.setText(PrefHelper.get().getApiNameIfEmptyDefault());
            editText.setPadding(48, 48, 48, 48);
            new AlertDialog.Builder(this)
                    .setView(editText)
                    .setMessage(getString(R.string.hint_api))
                    .setPositiveButton("Go", (dialogInterface, i) -> {
                        PrefHelper.get().setApiName(editText.getText().toString().replaceAll(" ", ""));
                        getData();
                    })
                    .create()
                    .show();
        });
        shareKit.show(mLastTouchPoint.x, mLastTouchPoint.y);

    }

    private DateCardAdapter.MyHolder mHolder = null;
    private int mLastPosition;

    @Override
    protected void onResume() {
        super.onResume();
        if (mHolder != null) {
            mHolder.updateText(mList.get(mLastPosition));
            mHolder = null;
        }
        mHasGoOtherPage = false;

        if (mRandom.nextInt(365) < 14 && !PrefHelper.get().getBonusMusic()) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.hint_music_secret))
                    .setCancelable(false)
                    .setNegativeButton(R.string.no, null)
                    .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                        new AlertDialog.Builder(this)
                                .setMessage(getString(R.string.secret_music))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.wonderful), null)
                                .create().show();
                    })
                    .create().show();
        }
    }

    private void openMusic() {
        if (mMusicList != null)
            return;
        if (PrefHelper.get().getBonusMusic()) {
            Logger.i("Request music");
            new RxPermissions(this)
                    .request(
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                                    ? Manifest.permission.READ_MEDIA_AUDIO // Android 13+ 适配新权限
                                    : Manifest.permission.READ_EXTERNAL_STORAGE // Android 12 及以下
                    )
                    .observeOn(Schedulers.io())
                    .map(aBoolean -> {
                        Logger.i("获取音乐权限 "  + aBoolean);
                        if (aBoolean)
                            return MusicUtil.getMusicList(MainActivity.this);
                        return null;
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseObserver<List<MusicBean>>(getLifecycle()) {
                        @Override
                        protected void onSucceed(List<MusicBean> result) {
                            mMusicList = result;
                            Logger.i("获取音乐数目 " + result.size());
                            if (!result.isEmpty()) {
                                Logger.i("音乐路径" + result.get(0).filePath);
                            }
                        }
                    });
        }
    }

    private void closeMusic() {
        if (mMusicList != null) {
            mMusicList.clear();
            mMusicList = null;
        }
    }

    private boolean mHasGoOtherPage = false;

    @Override
    public void onCardClick(int position, DateCardAdapter.MyHolder holder) {
        mPwdList.add(position + 1);
        if (checkPwd())
            return;
        mHolder = holder;
        mLastPosition = position;
        MusicBean musicBean = null;
        if (mMusicList != null && !mMusicList.isEmpty()) {
            musicBean = mMusicList.get(mRandom.nextInt(mMusicList.size()));
        }
        if (mHasGoOtherPage) {
            return;
        }
        mHasGoOtherPage = true;
        DetailActivity.go(this, mList.get(position), musicBean, holder.binding.bigIv);
    }

    @Override
    public void onDateLongClick(int position, DateCardAdapter.MyHolder holder) {
        DateCardEntity entity = mList.get(position);
        Date date = entity.getDate();
        if (date == null)
            return;
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        String solarStr = dateFormat.format(date); // 格式化日期
        String title = entity.holiday;
        if (title == null || title.isEmpty()) {
            title = entity.solarTerm;
        }
        String starStr = DateUtil.getConstellation(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String lunarStr = entity.lunar;
        String showStr = "\n" + solarStr + " \t" + starStr + " \n\n" + lunarStr + " \t" + entity.solarTerm + "\n";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(showStr)
                .create().show();
    }

    private boolean checkPwd() {
        if (mPwdList.size() < 7) {
            return false;
        }

        List<Integer> lastSeven = mPwdList.subList(mPwdList.size() - 7, mPwdList.size());

        // 校验最后 7 位
        if (areAllNumbersEqual(lastSeven)) {
            mPwdList.clear();
            return true;
        }
        // 校验最后 7 位
        if (isStrictlyIncreasing(lastSeven)) {
            mPwdList.clear();
            PrefHelper.get().setBonusMusic(true);
            openMusic();
            App.get().playSound(R.raw.piano);
            return true;
        }
        // 校验最后 7 位
        if (isStrictlyDecreasing(lastSeven)) {
            mPwdList.clear();
            PrefHelper.get().setBonusMusic(false);
            closeMusic();
            App.get().playSound(R.raw.xu);
            return true;
        }
        // 总长度大于 127 时，保留最后7位
        if (mPwdList.size() > 128) {
            mPwdList = lastSeven;
        }
        return false;
    }

    /**
     * 判断是否严格递增
     */
    private boolean isStrictlyIncreasing(List<Integer> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) + 1 != list.get(i + 1)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否所有数字相同
     */
    private boolean areAllNumbersEqual(List<Integer> list) {
        int first = list.get(0);
        for (int num : list) {
            if (num != first) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否严格递减
     */
    private boolean isStrictlyDecreasing(List<Integer> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) - 1 != list.get(i + 1)) {
                return false;
            }
        }
        return true;
    }

}
