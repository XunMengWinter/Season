package top.wefor.season.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.orhanobut.logger.Logger;

import top.wefor.season.App;
import top.wefor.season.util.CommonUtil;

/**
 * Created on 2018/12/7.
 *
 * @author ice
 */
public class NetConnReceiver extends BroadcastReceiver {

    public static String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    public void register(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NetConnReceiver.ACTION);
        context.registerReceiver(this, intentFilter);
        Logger.i("register");
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
        Logger.i("unregister");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
            return;

        Logger.i("onReceive " + intent.getAction());
        if (ACTION.equals(intent.getAction())) {
            Logger.i(CommonUtil.isNetworkConnected(App.get()) + " isConnected");
        }
    }


}
