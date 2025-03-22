package top.wefor.season.data.glide;

import android.graphics.BitmapFactory;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;

import java.io.File;
import java.io.IOException;

/**
 * Created on 2018/12/23.
 *
 * @author ice
 */
public class BitmapSizeDecoder implements ResourceDecoder<File, BitmapFactory.Options> {
    @Override
    public boolean handles(File source, Options options) throws IOException {
        return true;
    }

    @Override
    public Resource<BitmapFactory.Options> decode(File source, int width, int height, Options options) throws IOException {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(source.getAbsolutePath(), bmOptions);
        return new SimpleResource(bmOptions);
    }
}
