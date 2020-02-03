package com.example.portraitjava1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.nio.ByteBuffer;

//noteList用のclass ここの内容をlistViewにセットしている
public class Note {

    protected int id;
    protected String note;
    protected int modelId;
    protected int modelIdNum;
    protected String modelName;
    protected int modelInsertNum;
    protected byte[] thumbnail;
    protected String created;

    public Note(int modelId, int modelIdNum,String modelName, int modelInsertNum,byte[] thumbnail,String created) {
        this.modelId = modelId;
        this.modelIdNum = modelIdNum;
        this.modelName = modelName;
        this.modelInsertNum = modelInsertNum;
        this.thumbnail= thumbnail;
        this.created = created;
    }

    public String getNote() {
        return note;
    }

    public int getModelId() {
        return modelId;
    }

    public int getModelIdNum () {
        return modelIdNum ;
    }

    public String getModelName() {
        return modelName;
    }

    public int getModelInsertNum() {
        return modelInsertNum;
    }

    //DBはblob(中身はbase64encode済みのbyte[])、表示以外の処理中はbyte[]なのでここでbyte[]からbmpに変換してから返す。
    public Bitmap getThumbnail() {
        Bitmap bitmap = BitmapFactory.decodeByteArray(this.thumbnail, 0, this.thumbnail.length);
        return bitmap;
    }

    public String getCreated() {
        return created;
    }


}