package top.wefor.season.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import top.wefor.season.data.model.MusicBean;

/**
 * 工具类：快速获取本地音乐列表和专辑封面
 *
 * @author ice
 */
public class MusicUtil {

    /**
     * 获取本地音乐列表
     *
     * @param context 上下文
     * @return 音乐列表
     */
    public static List<MusicBean> getMusicList(Context context) {
        List<MusicBean> list = new ArrayList<>();

        // 只查询需要的字段，提高性能
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.IS_MUSIC
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"; // 过滤掉非音乐文件

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER)) {

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MusicBean music = new MusicBean();
                    music.id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    music.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    music.artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    music.filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    music.length = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                    int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    music.imageFilePath = getAlbumImage(context, albumId);

                    list.add(music);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Logger.e("Error fetching music list: " + e.getMessage());
        }

        return list;
    }

    /**
     * 获取专辑封面路径
     *
     * @param context 上下文
     * @param albumId 专辑 ID
     * @return 专辑封面路径
     */
    private static String getAlbumImage(Context context, int albumId) {
        String albumArtPath = "";

        String[] projection = {MediaStore.Audio.Albums.ALBUM_ART};
        String selection = MediaStore.Audio.Albums._ID + "=?";
        String[] selectionArgs = {String.valueOf(albumId)};

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null)) {

            if (cursor != null && cursor.moveToFirst()) {
                albumArtPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART));
            }
        } catch (Exception e) {
            Logger.e("Error fetching album art: " + e.getMessage());
        }

        return albumArtPath;
    }
}