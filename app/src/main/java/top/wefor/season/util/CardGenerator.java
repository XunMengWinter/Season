package top.wefor.season.util;

import java.util.Date;

import top.wefor.season.App;
import top.wefor.season.BuildConfig;
import top.wefor.season.R;
import top.wefor.season.data.model.DateCardEntity;
import top.wefor.season.data.tinker.TinkerUtil;

/**
 * Created on 2018/12/27.
 *
 * @author ice
 */
public class CardGenerator {

    public static DateCardEntity getIce() {
        DateCardEntity developer = new DateCardEntity();
        developer.title = "ice";
        developer.subtitle = "Developer";
        developer.desc = "A coder who loves art. \nWork for zzz.pet";
        try {
            developer.desc += " 🐈";
        } catch (Exception e) {
            e.printStackTrace();
        }
        developer.datestamp = 931115;
        developer.imageRes = R.drawable.img_jumao;
        developer.qrCode = "https://github.com/XunMengWinter";
        developer.stable = true;
        return developer;
    }

    public static DateCardEntity getSeason() {
        DateCardEntity app = new DateCardEntity();
        app.title = "Season";
        app.subtitle = "Illustration Calendar";
        app.desc = BuildConfig.VERSION_NAME + " " + BuildConfig.APK_DATE + " " + (BuildConfig.DEBUG ? "debug" : "release")
                + "\n\n" + ChannelHelper.get().getChannel() + " " + TinkerUtil.getTinkerId() + "-" + TinkerUtil.getTinkerVersion()
        ;
        app.datestamp = 241217;
        app.qrCode = "https://github.com/XunMengWinter/Season";
        app.imageRes = R.drawable.img_forrest;
        app.stable = true;
        return app;
    }

    public static DateCardEntity getError(String errorMessage) {
        DateCardEntity app = new DateCardEntity();
        app.title = "Error";
        app.subtitle = "error card";
        app.desc = "This is an error card, means you get nothing from current api. \n\nError message: " + errorMessage;
        try {
            app.datestamp = Integer.parseInt(App.sParseDateFormat.format(new Date()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        app.qrCode = "https://github.com/XunMengWinter/Season/issues";
        app.imageRes = R.drawable.ic_cd_max;
        app.stable = true;
        return app;
    }

}
