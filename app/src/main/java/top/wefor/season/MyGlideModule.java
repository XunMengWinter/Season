package top.wefor.season;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import androidx.annotation.NonNull;

/**
 * Created on 2018/12/27.
 *
 * @author ice
 */
@GlideModule
public class MyGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, GlideBuilder builder) {
        long memoryCacheSizeBytes = 2L * 1024 * 1024 * 1024; // 2GB
        long diskCacheSizeBytes = 4L * 1024 * 1024 * 1024; // 4GB
        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
        builder.setDiskCache(new DiskLruCacheFactory(context.getExternalCacheDir().getAbsolutePath(), diskCacheSizeBytes));
        RequestOptions requestOptions = new RequestOptions()
                .format(DecodeFormat.PREFER_RGB_565);
        builder.setDefaultRequestOptions(requestOptions);
    }
}