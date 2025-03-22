package top.wefor.season.data.http;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 一个简单的Http请求封装（业务使用过多的话需重构为RxJava+Retrofit）。
 * <p>
 * Created on 2018/11/3.
 *
 * @author ice
 */
public class HttpRequester {

    private final OkHttpClient mOkHttpClient;

    public HttpRequester() {
        mOkHttpClient = new OkHttpClient();
    }

    public void get(String url, ResponseCallback callback) {
        HttpTask httpTask = new HttpTask(mOkHttpClient, null, callback);
        httpTask.execute(url);
    }

    public void post(String url, String postJson, ResponseCallback callback) {
        if (postJson == null)
            postJson = "";
        HttpTask httpTask = new HttpTask(mOkHttpClient, postJson, callback);
        httpTask.execute(url);
    }


    private static class HttpTask extends AsyncTask<String, Void, String> {

        private final OkHttpClient client;
        private volatile String postJson;
        private ResponseCallback callback;

        private static final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");


        public HttpTask(OkHttpClient client, String postJson, ResponseCallback callback) {
            this.client = client;
            this.postJson = postJson;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder()
                    .url(strings[0]);
            if (this.postJson != null) {
                /*走post*/
                RequestBody body = RequestBody.create(JSON, this.postJson);
                builder.post(body);
            }
            try {
                Response response = this.client.newCall(builder.build()).execute();
                if (response.body() != null) {
                    return response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (this.callback != null)
                this.callback.response(s);
        }
    }

}
