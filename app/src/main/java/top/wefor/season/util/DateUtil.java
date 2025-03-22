package top.wefor.season.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import top.wefor.season.App;

/**
 * Created on 2018/12/24.
 *
 * @author ice
 */
public class DateUtil {

    public static String getWeekStr(int week) {
        String weekStr = "";
        switch (week) {
            case 1:
                weekStr = "Mon.";
                break;
            case 2:
                weekStr = "Tues.";
                break;
            case 3:
                weekStr = "Wed.";
                break;
            case 4:
                weekStr = "Thur.";
                break;
            case 5:
                weekStr = "Fri.";
                break;
            case 6:
                weekStr = "Sat.";
                break;
            case 7:
                weekStr = "Sun.";
                break;
        }
        return weekStr;
    }

    public static String getMonthStr(int month) {
        return switch (month) {
            case 1 -> "Jan.";
            case 2 -> "Feb.";
            case 3 -> "Mar.";
            case 4 -> "Apr.";
            case 5 -> "May.";
            case 6 -> "Jun.";
            case 7 -> "Jul.";
            case 8 -> "Aug.";
            case 9 -> "Sept.";
            case 10 -> "Oct.";
            case 11 -> "Nov.";
            case 12 -> "Dec.";
            default -> "";
        };
    }

    //    public static final String[] constellationArr = {"♒️ 水瓶座", "♓️ 双鱼座", "♈️ 白羊座", "♉️ 金牛座", "♊️ 双子座", "♋️ 巨蟹座", "♌️ 狮子座", "♍️ 处女座", "♎️ 天秤座", "♏️ 天蝎座", "♐️ 射手座", "♑️ 魔羯座"};
    public static final String[] constellationArr = {"♒️", "♓️", "♈️", "♉️", "♊️", "♋️", "♌️", "♍️", "♎️", "♏️", "♐️", "♑️"};

    public static final int[] constellationEdgeDay = {20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};

    /**
     * 根据日期获取星座
     *
     * @return
     */
    public static String getConstellation(Date date) {
        if (date == null) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (day < constellationEdgeDay[month]) {
            month = month - 1;
        }
        if (month >= 0) {
            return constellationArr[month];
        }
        // default to return 魔羯
        return constellationArr[11];
    }

    public final static String[] solarTerm = {
            "小寒", "大寒", "立春", "雨水", "惊蛰", "春分",
            "清明", "谷雨", "立夏", "小满", "芒种", "夏至",
            "小暑", "大暑", "立秋", "处暑", "白露", "秋分",
            "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"
    };

//    立春	2月4日 11:14:14	雨水	2月19日 07:03:51	惊蛰	3月6日 05:09:39
//    春分	3月21日 05:58:20	清明	4月5日 09:51:21	谷雨	4月20日 16:55:10
//    立夏	5月6日 03:02:40	小满	5月21日 15:59:01	芒种	6月6日 07:06:18
//    夏至	6月21日 23:54:09	小暑	7月7日 17:20:25	大暑	7月23日 10:50:16
//    立秋	8月8日 03:12:57	处暑	8月23日 18:01:53	白露	9月8日 06:16:46
//    秋分	9月23日 15:50:02	寒露	10月8日 22:05:32	霜降	10月24日 01:19:37
//    立冬	11月8日 01:24:15	小雪	11月22日 22:58:48	大雪	12月7日 18:18:21
//    冬至	12月22日 12:19:18	小寒	1月5日 23:38:52	大寒	1月20日 16:59:27


}
