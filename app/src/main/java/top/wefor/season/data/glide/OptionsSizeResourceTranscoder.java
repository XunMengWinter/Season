package top.wefor.season.data.glide;

import android.graphics.BitmapFactory;
import android.util.Size;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;

/**
 * Created on 2018/12/23.
 *
 * @author ice
 */
public class OptionsSizeResourceTranscoder implements ResourceTranscoder<BitmapFactory.Options, Size> {
    public Resource<Size> transcode(Resource<BitmapFactory.Options> resource, Options options) {
        BitmapFactory.Options bmOptions = resource.get();
        Size size = new Size(bmOptions.outWidth, bmOptions.outHeight);
        return new SimpleResource(size);
    }

}
