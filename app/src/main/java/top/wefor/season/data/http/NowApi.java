package top.wefor.season.data.http;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import top.wefor.season.App;

/*
 * Thanks to
 * https://github.com/amitshekhariitbhu/RxJava2-Android-Samples
 *
 * Created by ice on 3/13/16.
 */
public final class NowApi {

    private static Retrofit.Builder get(String baseUrl) {
        return get(baseUrl, null);
    }

    public static SeasonApi getSeason() {
        return get(Urls.BASE_URL, App.get().getHttpCache()).build().create(SeasonApi.class);
    }

    private static Retrofit.Builder get(String baseUrl, Cache cache) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS);
        if (cache != null) {
            httpClientBuilder.cache(cache)
                    .addInterceptor(new CacheInterceptor())
                    .addNetworkInterceptor(new CacheNetworkInterceptor());
        }
        httpClientBuilder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Logger.i(request.toString());
                Response response = chain.proceed(request);
                Logger.i(response.toString());
                return response;
            }
        });
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.client(httpClientBuilder.build())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        ;

        return builder;
    }

}
