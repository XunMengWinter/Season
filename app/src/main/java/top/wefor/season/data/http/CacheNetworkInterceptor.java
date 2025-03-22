package top.wefor.season.data.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created on 2018/12/6.
 *
 * @author ice
 */
public class CacheNetworkInterceptor implements Interceptor {
    public Response intercept(Interceptor.Chain chain) throws IOException {
        //无缓存,进行缓存
        return chain.proceed(chain.request()).newBuilder()
                .removeHeader("Pragma")
                //对请求进行最大60秒的缓存
                .addHeader("Cache-Control", "max-age=60")
                .build();
    }
}
