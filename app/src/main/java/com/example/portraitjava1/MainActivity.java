package com.example.portraitjava1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONObject;

import static android.icu.text.DateTimePatternGenerator.PatternInfo.OK;

//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.AdRequest;


public class MainActivity extends AppCompatActivity implements OnClickListener {

    static final String thumnailURL = "http://portrait531.herokuapp.com/api/thumbnail";
    static DBAdapter dbAdapter;
    static NoteListAdapter listAdapter;
    static List<Note> noteList = new ArrayList<Note>();
    static SharedPreferences pref;

    TextView thumbTitleText,thumbModelNumText,thumbDateText;
    ImageView thumbnailImage;
    ListView itemListView;
    Button kickButton;

    private DownloadTask task;
    private JsonChange jsonChange;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        //初めてこのアプリを読み込んだ場合
        if (pref.getInt("START",0) == 0){
            //このアプリについて、を表示→OK押したらHTTPアクセスの開始
            firstCall(1);
        } else {
            //HTTPアクセスの開始
            taskAccess();
        }


    }

    @Override
    public void onResume() {
        super.onResume();

        //BitMapImageの初期化・・・これ要るんだっけ？
        GlobalVariable.initPhotoBM();

        //画面パーツの配置
        findViews();
        //listerを設定
        setListeners();

        //DBからデータを取得、このfunctionの中でlistadapterへの反映もしている。
//        loadNote();
    }

    protected void findViews() {

        setContentView(R.layout.activity_main);

        kickButton = (Button) findViewById(R.id.kickButton);
        dbAdapter = new DBAdapter(this);
        listAdapter = new NoteListAdapter();
        itemListView = (ListView) findViewById(R.id.itemListView);
        itemListView.setAdapter(listAdapter);

        //adsence未設定
//        AdView mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
    }

    //DBの中身を取得してnoteClassにセット。
    protected void loadNote() {
        noteList.clear();
        dbAdapter.open();
        Cursor c = dbAdapter.getAllNotes();
        if (c.moveToFirst()) {
            do {
                Note note = new Note(c.getInt(c.getColumnIndex(DBAdapter.COL_POST_MODELID)),
                        c.getInt(c.getColumnIndex(DBAdapter.COL_POST_MODELIDNUM)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_POST_MODELNAME)),
                        c.getInt(c.getColumnIndex(DBAdapter.COL_POST_MODELINSERTNUM)),
                        c.getBlob(c.getColumnIndex(DBAdapter.COL_POST_THUMBNAIL)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_POST_CREATED)));
                noteList.add(note);
            } while (c.moveToNext());
        }
        c.close();
        dbAdapter.close();
        listAdapter.notifyDataSetChanged();
    }

    protected void setListeners() {
        kickButton.setOnClickListener(this);

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Note note = noteList.get(position);
                //次画面(webView)を呼び出し
//                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                Intent intent = new Intent(MainActivity.this, Main3Activity.class);
                intent.putExtra("modelId", note.getModelId());
                intent.putExtra("modelIdNum", note.getModelIdNum());
                intent.putExtra("modelInsertNum", note.getModelInsertNum());
                Log.v("@Main_intent_insertnum", String.valueOf(note.getModelInsertNum()));
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kickButton:
                //HTTPアクセスの開始
                taskAccess();
                break;
        }
    }

    private class NoteListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return noteList.size();
        }

        @Override
        public Object getItem(int position) {
            return noteList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.rowlist, null);
            }
            Note note = (Note) getItem(position);
            if (note != null) {
                //viewのセット
                thumbTitleText = v.findViewById(R.id.thumbTitleText);
                thumbModelNumText = v.findViewById(R.id.thumbModelNumText);
                thumbnailImage = v.findViewById(R.id.thumbnailImage);
                thumbDateText = v.findViewById(R.id.thumbDateText);
                //viewにstringをセット
                thumbTitleText.setText(note.getModelName() + "　　Part" + note.getModelInsertNum());
                thumbDateText.setText("登録日：" + note.getCreated().substring(0,11));
                thumbModelNumText.setText("登録写真数："  + String.valueOf(note.getModelIdNum()) + "枚");
                thumbnailImage.setImageBitmap(note.getThumbnail());
                v.setTag(note);
                Log.v("@@@set", String.valueOf(note.getThumbnail()));
            }
            return v;
        }
    }


    // メニュー作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // メニューアイテム選択イベント
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // メニュー１選択時の処理
            case R.id.menuFirst:
                firstCall(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //データ取得後の処理
    private DownloadTask.Listener createListener() {
        return new DownloadTask.Listener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(String httpString) {

                Log.v("@@@",httpString);
                //httpstingはjsonデータ全件をstringにしているのでjsonに変換
                jsonChange = new JsonChange();
                JSONObject json = jsonChange.StringTOJson(httpString);

                //データ取得に成功したらDBにセット
                dbAdapter.open();
                String jsonDataOne,jsonDataModelId,jsonDataMaxNum,jsonDataModelName,jsonDataModelInsertNum,jsonDataImages,jsonDataDate;
                JSONObject jsonOne;

                for(int i=0;i < json.length();i++){
                    //jsonデータの{No.X{}}のNo.Xを指定
                    StringBuilder buf = new StringBuilder();
                    buf.append("No.");
                    buf.append(i);
                    //No.Xのjsonデータを取得してjson形式に変換
                    jsonDataOne = jsonChange.JsonGetValue(json,buf.toString());
                    jsonOne = jsonChange.StringTOJson(jsonDataOne);
                    //No.Xの各配列値を取得
                    jsonDataModelId = jsonChange.JsonGetValue(jsonOne,"modelId");
                    jsonDataMaxNum = jsonChange.JsonGetValue(jsonOne,"maxNum");
                    jsonDataModelName = jsonChange.JsonGetValue(jsonOne,"modelName");
                    jsonDataModelInsertNum = jsonChange.JsonGetValue(jsonOne,"modelInsertNum");
                    jsonDataDate = jsonChange.JsonGetValue(jsonOne,"date");
                    jsonDataImages = jsonChange.JsonGetValue(jsonOne,"images");
                    byte[] byteImage = Base64.decode(jsonDataImages,Base64.DEFAULT);
                    Log.v("@MA:json",jsonDataModelInsertNum);
                    //DBの構造はBLOB、データ格納はbyte[]、いきなりここで

//                    いきなりここでstring->byte[]で変換しているのが NGっぽい。string->base64decode->byte[]が必要。
                    dbAdapter.saveNote(Integer.parseInt(jsonDataModelId), Integer.parseInt(jsonDataMaxNum),
                            jsonDataModelName,Integer.parseInt(jsonDataModelInsertNum),byteImage, jsonDataDate);
                }

                dbAdapter.close();

                //再描画
                loadNote();
//                Toast.makeText(MainActivity.this, "リストを更新しました", Toast.LENGTH_SHORT).show();
            }
        };
    }

    protected void firstCall(int mode){

        final String stringFirstCall =
                "＜このアプリの権利関係について＞" + "\n" +
                "このアプリに掲載されている写真には肖像権／著作権が存在します。" + "\n" +
                "無断複製・無断転載等は禁止していますので、このアプリを通じた閲覧以外はご遠慮ください。" + "\n"  + "\n" +
                "＜このアプリの使用方法について＞" + "\n" +
                "リストをclickすると写真が表示されます。" + "\n" +
                "次の写真を閲覧するときは写真の右側をダブルタップしてください。" + "\n" +
                "（スワイプは認識してくれません。）"  + "\n" +
                "OKを押すと処理を進めます。"  + "\n" +
                "NGを押すと処理を停止します。";

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("初めにお読みください。");
        alertDialog.setMessage(stringFirstCall);
        if(mode == 1){
            //oncreateから呼び出しの場合はOK押したら操作を入れる。
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //初回FLGを折る
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("START",1);
                    editor.apply();
                    //HTTPアクセスの開始
                    taskAccess();
                }
            });
            alertDialog.setNegativeButton("NG", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    onDestroy();
                }
            });

        }else{
            //設定から呼び出したときはOKのみ。
            alertDialog.setPositiveButton("OK",null);
        }
        alertDialog.show();
    }

    protected void taskAccess(){
        task = new DownloadTask(MainActivity.this);
        task.setListener(createListener());
        task.execute(thumnailURL);
    }

}