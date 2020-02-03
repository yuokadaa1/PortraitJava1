package com.example.portraitjava1;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Void, String> {

    private Listener listener;
    ProgressDialog dialog;
    Context context;

    public DownloadTask(Context context) {
        this.context = context;
        Log.v("@Down_context:",context.toString());
    }

    protected void onPreExecute(){
        dialog = new ProgressDialog(context);
        dialog.setMessage("通信中・・・");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//くるくる
        dialog.show();
        return ;
    }

    // 非同期処理
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... params) {
        return downloadImage(params[0]) ;
    }

    // 途中経過をメインスレッドに返す
    @Override
    protected void onProgressUpdate(Void... progress) {
        //working cursor を表示させるようにしてもいいでしょう
    }

    // 非同期処理が終了後、結果をメインスレッドに返す
    @Override
    protected void onPostExecute(String httpString) {
        if (listener != null) {
            listener.onSuccess(httpString);
        }
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String downloadImage(String address) {
        String httpString = "";
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL( address );

            // HttpURLConnection インスタンス生成
            urlConnection = (HttpURLConnection) url.openConnection();

            // タイムアウト設定
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(20000);

            // リクエストメソッド
            urlConnection.setRequestMethod("GET");

            // リダイレクトを自動で許可しない設定
            urlConnection.setInstanceFollowRedirects(false);

            // ヘッダーの設定(複数設定可能)
            urlConnection.setRequestProperty("Accept-Language", "jp");

            // 接続
            urlConnection.connect();

            int resp = urlConnection.getResponseCode();

            switch (resp){
                case HttpURLConnection.HTTP_OK:
                    try(InputStream is = urlConnection.getInputStream()){
//                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
//                        StringBuilder sb = new StringBuilder();
//                        while ((httpString = br.readLine()) != null) {
//                            sb.append(httpString);
//                        }
//                        is.close();
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        while (true) {
                            int len = is.read(buffer);
                            if (len < 0) {break;}
                            out.write(buffer, 0, len);
                        }
                        httpString = new String(out.toByteArray());
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.d("debug", "downloadImage error");
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return httpString;
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onSuccess(String httpString);
    }

}