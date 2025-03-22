package top.wefor.season.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2018/11/28.
 *
 * @author ice
 */
public class CommonUtil {

    public static float getRatio(String ratio) {
        try {
            String[] nums = ratio.split("/");
            return Float.parseFloat(nums[0]) / Float.parseFloat(nums[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static String getSplitNumber(long num) {
        if (num < 1000) {
            return num + "";
        }

        String favorStr = num + "";
        final char[] chars = favorStr.toCharArray();
        final char[] newChar = new char[chars.length + (chars.length - 1) / 3];
        int index = newChar.length - 1;
        int oldRightIndex = 0;
        for (int i = chars.length - 1; i >= 0; i--) {
            newChar[index--] = chars[i];
            if (i == 0)
                break;
            if (oldRightIndex % 3 == 2) {
                newChar[index--] = ',';
            }
            oldRightIndex++;
        }
        return new String(newChar);
    }

    public static String getUrl(String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            if (!imageUrl.contains("http://") && !imageUrl.contains("https://")) {
                return "https://" + imageUrl;
            }
        }
        return imageUrl;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    /**
     * 跳转至系统设置页
     */
    public static void goSetting(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String getMacAddress(Context context) {
        String macAddress = getWifiMacAddress();
        if (TextUtils.isEmpty(macAddress))
            macAddress = android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
//        return "00:08:22:6A:69:2B";
        return macAddress;
    }

    /**
     * 获取mac地址
     *
     * @return p.s.如果得到大概是00:00:20，那肯定错了。
     */
    private static String getWifiMacAddress() {
        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
    }

    /**
     * 将文本复制到剪切板
     */
    public static boolean copyToClipboard(Activity activity, String text) {
        if (activity == null || text == null)
            return false;
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager)
                activity.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("text", text);
            clipboard.setPrimaryClip(clip);
            return true;
        }

        return false;
    }

}
