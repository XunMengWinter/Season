package top.wefor.season.util;

import android.text.TextUtils;

import top.wefor.season.App;
import top.wefor.season.R;

/**
 * Created on 2018/12/30.
 *
 * @author ice
 */
public class StringUtil {

    /**
     * 获取文字长度，折算为半角数。
     */
    public static int getTextLengthInDBC(String text) {
        if (TextUtils.isEmpty(text))
            return 0;

        char[] chars = text.toCharArray();
        int count = chars.length;
        for (char c : chars) {
            String len = Integer.toBinaryString(c);
            if (len.length() > 8) {
                count++;
            }
        }
        return count;
    }

    public static float getTitleSizeInPx(String title) {
        float titleSize = App.get().getResources().getDimension(R.dimen.text_title);
        int textLength = StringUtil.getTextLengthInDBC(title);
        if (textLength > 8) {
            if (textLength > 12)
                titleSize = titleSize * 3f / 4;
            else
                titleSize = (1 - (1f / 4) * (1f / 4) * (textLength - 8)) * titleSize;
        }
        return titleSize;
    }

}
