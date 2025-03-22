package top.wefor.season.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import top.wefor.season.App;


/**
 * 用于存储键值对
 * <p>
 * Created on 2018/10/31.
 *
 * @author ice
 */
public class PrefHelper {

    private static final String PREF_NAME = "season_pref";

    private static final String TINKER_ID = "tinker_id";

    //上次合成补丁的时间（需要重启才能生效）
    private static final String LAST_PATCH_TIME = "last_patch_time";

    private static final String DIY_PADDING = "diy_padding";
    private static final String DIY_QR_CODE_SHOW = "diy_qr_code_show";

    private static final String LAST_POSITION = "last_position";

    private static final String BONUS_MUSIC = "bonus_music";

    private static final String API_NAME = "api_name";

    private static volatile PrefHelper sPrefHelper;

    private SharedPreferences mSharedPreferences;

    private PrefHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static PrefHelper get() {
        if (sPrefHelper == null) {
            synchronized (PrefHelper.class) {
                if (sPrefHelper == null)
                    sPrefHelper = new PrefHelper(App.get());
            }
        }
        return sPrefHelper;
    }

    public String getTinkerId() {
        return mSharedPreferences.getString(TINKER_ID, "");
    }

    @SuppressLint("ApplySharedPref")
    public void setTinkerId(String tinkerId) {
        mSharedPreferences.edit().putString(TINKER_ID, tinkerId).commit();
    }

    public String getApiNameIfEmptyDefault() {
        String api = mSharedPreferences.getString(API_NAME, "");
        if (TextUtils.isEmpty(api))
            api = "all.json";
        return api;
    }

    public String getCat2025(){
        return "cat_2025.json";
    }

    @SuppressLint("ApplySharedPref")
    public void setApiName(String apiName) {
        mSharedPreferences.edit().putString(API_NAME, apiName).commit();
    }

    public long getLastPatchTime() {
        return mSharedPreferences.getLong(LAST_PATCH_TIME, 0);
    }

    public void setLastPatchTime(long ms) {
        mSharedPreferences.edit().putLong(LAST_PATCH_TIME, ms).apply();
    }

    public int getDiyPadding() {
        return mSharedPreferences.getInt(DIY_PADDING, 0);
    }

    public void setDiyPadding(int padding) {
        mSharedPreferences.edit().putInt(DIY_PADDING, padding).apply();
    }

    public boolean getDiyQrCodeShow() {
        return mSharedPreferences.getBoolean(DIY_QR_CODE_SHOW, true);
    }

    public void setDiyQrCodeShow(boolean qrCodeShow) {
        mSharedPreferences.edit().putBoolean(DIY_QR_CODE_SHOW, qrCodeShow).apply();
    }

    public int getLastPosition() {
        return mSharedPreferences.getInt(LAST_POSITION, 0);
    }

    /**
     * 上次停留的位置
     */
    @SuppressLint("ApplySharedPref")
    public void setLastPosition(int position) {
        mSharedPreferences.edit().putInt(LAST_POSITION, position).commit();
    }

    public boolean getBonusMusic() {
        return mSharedPreferences.getBoolean(BONUS_MUSIC, false);
    }

    @SuppressLint("ApplySharedPref")
    public void setBonusMusic(boolean bonusMusic) {
        mSharedPreferences.edit().putBoolean(BONUS_MUSIC, bonusMusic).commit();
    }
}
