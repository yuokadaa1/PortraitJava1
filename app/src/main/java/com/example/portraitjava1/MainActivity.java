package com.example.portraitjava1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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

//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.AdRequest;


public class MainActivity extends AppCompatActivity implements OnClickListener {

    static final String thumnailURL = "http://portrait531.herokuapp.com/api/thumbnail";
    static final String photoURL = "http://portrait531.herokuapp.com/api/data";
    static DBAdapter dbAdapter;
    static NoteListAdapter listAdapter;
    static List<Note> noteList = new ArrayList<Note>();

    TextView thumbTitleText,thumbModelNumText,thumbDateText;
    ImageView thumbnailImage;
    ListView itemListView;
    Button kickButton;

    private DownloadTask task;
    private JsonChange jsonChange;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ntent.ACTION_SENDの受け取り http://techbooster.jpn.org/andriod/application/1388/
        //HTTPアクセスの開始
        task = new DownloadTask(this);
        task.setListener(createListener());
        task.execute(thumnailURL);
    }

    @Override
    public void onResume() {
        super.onResume();

        GlobalVariable.initPhotoBM();

        //部品の配置
        setContentView(R.layout.activity_main);
        //画面パーツの配置
        findViews();
        //listerを設定
        setListeners();

        //DBからデータを取得、このfunctionの中でlistadapterへの反映もしている。
//        loadNote();
    }

    protected void findViews() {
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
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
//                Intent intent = new Intent(MainActivity.this, Main3Activity.class);
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
                task = new DownloadTask(this);
                task.setListener(createListener());
                task.execute(thumnailURL);
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
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            //case R.id.action_settings:
//            case R.id.menu_item01:
//                // メニュー１選択時の処理
//                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.menu_item02:
//                new AlertDialog.Builder(MainActivity.this).setIcon(R.drawable.icon).setTitle("登録してある情報を全て削除します。よろしいですか。")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dbAdapter.open();
//                                dbAdapter.deleteAllNotes();
//                                dbAdapter.close();
//                                File dAllfile[] = new File(MainActivity.this.getFilesDir().getAbsolutePath()).listFiles();
//                                if (dAllfile != null) {
//                                    for (int i = 0; i < dAllfile.length; i++) {
//                                        if (dAllfile[i].isFile() && dAllfile[i].getName().endsWith("txt")) {
//                                            dAllfile[i].delete();
//                                        }
//                                    }
//                                }
//                                loadNote();
//                            }
//                        }).setNegativeButton("Cancel", null).show();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }


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


}