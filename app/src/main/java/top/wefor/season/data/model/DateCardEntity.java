package top.wefor.season.data.model;

import android.graphics.drawable.GradientDrawable;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.util.Date;

import androidx.annotation.DrawableRes;

import top.wefor.season.App;

/**
 * Created on 2018/12/21.
 *
 * @author ice
 */
public class DateCardEntity {

    @SerializedName("title")
    public String title;
    @SerializedName("subtitle")
    public String subtitle;
    @SerializedName("desc")
    public String desc;

    /**
     * yyMMdd
     */
    @SerializedName("datestamp")
    public int datestamp;

    @SerializedName("lunar")
    public String lunar = "";

    @SerializedName("solarTerm")
    public String solarTerm = "";

    @SerializedName("holiday")
    public String holiday;

    @SerializedName("day")
    public String day;

    @SerializedName("weekday")
    public int weekday;
    @SerializedName("month")
    public String month;

    @SerializedName("imageUrl")
    public String imageUrl;

    @SerializedName("qr_code")
    public String qrCode;

    @SerializedName("image_res")
    @DrawableRes
    public int imageRes;

    public int mImageW;
    public int mImageH;

    //    public int mTitleColor;
    public int mSubtitleColor;
    public int mDetailColor;
    public int mDayColor;
    public int mMonthColor;
    public int mWeekColor;
    public transient GradientDrawable mGradientDrawable;

    public boolean stable = false;

    private boolean isDateValid() {
        return (datestamp + "").length() == 8;
    }

    private static String[] weekMap = {"", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天"};

    public String getWeekStr() {
        String weekStr = "";
        try {
            weekStr = weekMap[weekday];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return weekStr;
    }

    public String getMontStr() {
        if (holiday != null && !holiday.isEmpty()) {
            return holiday;
        }
        if (solarTerm != null && !solarTerm.isEmpty()) {
            return solarTerm;
        }
        if (month != null) {
            return month;
        }
        return "";
    }

    public Date getDate() {
        if (isDateValid()) {
            try {
                return App.sParseDateFormat.parse(datestamp + "");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return new Date();
    }

}
