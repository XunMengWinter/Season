package top.wefor.season.util;

import com.orhanobut.logger.Logger;

/**
 * 不同渠道需求定制。
 * <p>
 * Created on 2018/12/17.
 *
 * @author ice
 */
public class ChannelHelper {

    private volatile static ChannelHelper sChannelHelper;

    private String mChannel;

    private ChannelHelper() {
        mChannel = "channel";
        Logger.i("channel: " + mChannel);
    }

    public static ChannelHelper get() {
        if (sChannelHelper == null) {
            synchronized (ChannelHelper.class) {
                if (sChannelHelper == null) {
                    sChannelHelper = new ChannelHelper();
                }
            }
        }
        return sChannelHelper;
    }

    public String getChannel() {
        return mChannel;
    }

}
