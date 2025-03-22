package top.wefor.season.data.http;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import top.wefor.season.App;
import top.wefor.season.util.CommonUtil;

/**
 * Created on 2018/12/6.
 *
 * @author ice
 */
public class CacheInterceptor implements Interceptor {
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Response resp;
        Request req;
        if (CommonUtil.isNetworkConnected(App.get())) {
            //有网络,检查10秒内的缓存
            req = chain.request()
                    .newBuilder()
                    .cacheControl(new CacheControl
                            .Builder()
                            .maxAge(10, TimeUnit.SECONDS)
                            .build())
                    .build();
        } else {
            //无网络,检查30天内的缓存,即使是过期的缓存
            req = chain.request().newBuilder()
                    .cacheControl(new CacheControl.Builder()
                            .onlyIfCached()
                            .maxStale(30, TimeUnit.DAYS)
                            .build())
                    .build();
            Logger.i(req.url().toString() + " --read cache");
        }
        resp = chain.proceed(req);
        return resp.newBuilder().build();
    }
}