package com.example.portraitjava1;

//Main2Activityはfragsmentで自力作成してしまったので、ImageFreeShowerViewを呼び出す
//classを作ってみる。
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.portraitjava1.ImageFreeShowerView;

import org.json.JSONObject;

import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity {

    private DownloadTask task;
    private JsonChange jsonChange;
    static final String photoURL = "http://portrait531.herokuapp.com/api/data";
    static private ArrayList<Bitmap> photoBM = new ArrayList<Bitmap>();
    ImageFreeShowerView mImageFreeShowerViewMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        StringBuilder buf = new StringBuilder();
        buf.append(photoURL);
        buf.append("/");
        buf.append(intent.getIntExtra("modelId",0));
        buf.append("/");
        buf.append(intent.getIntExtra("modelInsertNum",0));
        Log.v("@M2A-oncre-buf:", String.valueOf(buf));

        task = new DownloadTask(this);
        task.setListener(createListener());
        task.execute(String.valueOf(buf));

    }

    private void setViews() {
        setContentView(R.layout.activity_main3);
        mImageFreeShowerViewMain = findViewById(R.id.imageFreeShowerView_main);
        mImageFreeShowerViewMain.setBitmapList(photoBM);
    }

    //データ取得後の処理
    private DownloadTask.Listener createListener() {
        return new DownloadTask.Listener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(String httpString) {

                Log.v("@Main2.downloadtask:",httpString);
                //httpstingはjsonデータ全件をstringにしているのでjsonに変換
                jsonChange = new JsonChange();
                JSONObject json = jsonChange.StringTOJson(httpString);
                byte[] byteImage = new byte[0];

                String jsonDataOne,jsonDataImages = null,jsonDataDate;
                JSONObject jsonOne;

                for(int i=0;i < json.length();i++) {
                    //jsonデータの{No.X{}}のNo.Xを指定
                    StringBuilder buf = new StringBuilder();
                    buf.append("No.");
                    buf.append(i);
                    //No.Xのjsonデータを取得してjson形式に変換
                    jsonDataOne = jsonChange.JsonGetValue(json, buf.toString());
                    jsonOne = jsonChange.StringTOJson(jsonDataOne);
                    //No.Xの各配列値を取得、画像の配列に格納する。
                    jsonDataImages = jsonChange.JsonGetValue(jsonOne, "images");
                    byteImage = Base64.decode(jsonDataImages, Base64.DEFAULT);
                    photoBM.add(i, BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length));
                    GlobalVariable.setPhotoBM(i,BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length));
                }
//                imageView.setImageBitmap(photoBM.get(0));
//                imageView.setImageBitmap(GlobalVariable.getPhotoBM(0));

                setViews();
                Log.v("@M2.getPhotoBM",GlobalVariable.getPhotoBM(0).toString());
                Log.v("@Main2.1",jsonDataImages);
                Log.v("@Main2.2", String.valueOf(byteImage));
                Log.v("@Main2.3","通信の完了");
            }
        };
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            Log.d("debug","The system bars are visible");
                        } else {
                            Log.d("debug","The system bars are NOT visible");
                        }
                    }
                });
    }
}