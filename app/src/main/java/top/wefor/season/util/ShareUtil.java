package top.wefor.season.util;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class ShareUtil {

    public static final int TYPE_WECHAT = 7;
    public static final int TYPE_WECHAT_TIMELINE = 8;
    public static final int TYPE_GALLERY = 10;

    public static void share(Context context, Bitmap bmp, int type) {
        switch (type) {
            case TYPE_WECHAT:
                shareToWechat(context, bmp, false);
                break;
            case TYPE_WECHAT_TIMELINE:
                shareToWechat(context, bmp, true);
                break;
            case TYPE_GALLERY:
                saveImageToGallery(context, bmp);
                break;
            default:
                Toast.makeText(context, "Invalid share type", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public static void saveImageToGallery(Context context, Bitmap bitmap) {
        String fileName = "Season_Image_" + System.currentTimeMillis() + ".png"; // 生成唯一文件名
        OutputStream imageOutStream = null;

        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

            // Android 10 (API 29) 及以上使用 Scoped Storage
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SeasonApp");
                values.put(MediaStore.Images.Media.IS_PENDING, true);

                // 插入 MediaStore
                Uri imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (imageUri != null) {
                    imageOutStream = context.getContentResolver().openOutputStream(imageUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, imageOutStream);
                    imageOutStream.flush();
                    imageOutStream.close();

                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    context.getContentResolver().update(imageUri, values, null, null);

                    Toast.makeText(context, "图片已保存至相册", Toast.LENGTH_SHORT).show();
                } else {
                    throw new FileNotFoundException("无法保存图片，imageUri 为空");
                }
            } else {
                // Android 9 及以下版本使用传统方式
                String imagePath = MediaStore.Images.Media.insertImage(
                        context.getContentResolver(),
                        bitmap,
                        fileName,
                        "Season App Image"
                );
                if (imagePath != null) {
                    Toast.makeText(context, "图片已保存至相册", Toast.LENGTH_SHORT).show();
                } else {
                    throw new FileNotFoundException("图片保存失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "保存图片出错: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (imageOutStream != null) {
                    imageOutStream.close();
                }
            } catch (IOException ignored) { }
        }
    }
    public static void shareToWechat(Context context, Bitmap bmp, boolean isTimeLine) {
        // 微信分享逻辑保持不变
    }
}