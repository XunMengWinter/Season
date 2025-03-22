package top.wefor.season.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;

/**
 * Created on 2018/12/21.
 *
 * @author ice
 */
public class ImageUtil {

    public static String saveImage(Context context, Bitmap bitmap) {
        return MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Season", "Season_" + System.currentTimeMillis());
    }


//    public static boolean saveImage(Context context, Bitmap bitmap) {
//        String fileName = "season-" + System.currentTimeMillis() + ".jpg";
//        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File file = new File(dir, fileName);
//        bitmapToFile(bitmap, file);
//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri uri = Uri.fromFile(file);
//        intent.setData(uri);
//        context.sendBroadcast(intent);//这个广播的目的就是更新图库
//        return true;
//    }

//    public static boolean bitmapToFile(Bitmap bitmap, File file) {
//        try {
//            FileOutputStream outStream = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//            outStream.flush();
//            outStream.close();
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    /**
     * pick image
     * https://developer.android.com/guide/topics/providers/document-provider
     */
    public static Intent pickImageIntent() {
        Intent intent = new Intent();
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

//        }
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return intent;
    }

}
