package com.example.portraitjava1;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.CheckBox;

public class SettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //プリファレンス　アクセス
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        //読み込み
        int buttonOnOff1 = pref.getInt("setButton1", 0);
        int buttonOnOff2 = pref.getInt("setButton2", 0);

        if (buttonOnOff2 == 1) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_setting);
        // チェックボックスのオブジェクトを取得
        CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkbox1);
        CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkbox2);

        if (buttonOnOff1 == 0) {
            checkBox1.setChecked(false);
        } else {
            checkBox1.setChecked(true);
        }

        if (buttonOnOff2 == 0) {
            checkBox2.setChecked(false);
        } else {
            checkBox2.setChecked(true);
        }

        // チェック状態を取得
        boolean isChecked1 = checkBox1.isChecked();
        boolean isChecked2 = checkBox2.isChecked();

        if (isChecked1 == true) {
            Log.v("あああああああ", "ボタン１チェック");
        } else {
            Log.v("あああああああ", "ボタン１noチェック");
        }

        if (isChecked2 == true) {
            Log.v("あああああああ", "ボタン2チェック");
        } else {
            Log.v("あああああああ", "ボタン2noチェック");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // チェックボックスのオブジェクトを取得
        CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkbox1);
        CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkbox2);

        // チェック状態を取得
        boolean isChecked1 = checkBox1.isChecked();
        boolean isChecked2 = checkBox2.isChecked();

        //保存
        //プリファレンス　アクセス
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        if (isChecked1 == true) {
            editor.putInt("setButton1", 1);
        } else {
            editor.putInt("setButton1", 0);
        }
        if (isChecked2 == true) {
            editor.putInt("setButton2", 1);
        } else {
            editor.putInt("setButton2", 0);
        }
        editor.commit();
    }
}
