package top.wefor.season.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import top.wefor.season.App;
import top.wefor.season.R;
import top.wefor.season.ui.DetailActivity;
import top.wefor.season.ui.EmptyFinishActivity;

/**
 * Created on 2018/12/29.
 *
 * @author ice
 */
public class MusicNotification {
    private static final String CHANNEL_ID = "112";

    public static void createNotification(Context context, String title, String subtitle) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(App.get(), CHANNEL_ID);
        Intent ii = new Intent(App.get(), EmptyFinishActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, PendingIntent.FLAG_IMMUTABLE);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_cat_hm);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(subtitle);

        NotificationManager notificationManager =
                (NotificationManager) App.get().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(CHANNEL_ID);
        }
        notificationManager.notify(121, mBuilder.build());
    }


    public static void cancelNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.get());
        // notificationId is a unique int for each notification that you must define
        notificationManager.cancel(121);
    }

}
