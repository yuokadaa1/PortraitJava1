package com.example.portraitjava1;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

public class GlobalVariable {

    private static ArrayList<Bitmap> gPhotoBM = new ArrayList<Bitmap>();


    public static void initPhotoBM(){
        gPhotoBM.clear();
//        gPhotoBM = new ArrayList<Bitmap>();
    }

    public static void setPhotoBM(int i,Bitmap inPhotoBM){
        Log.v("@GVMset:", String.valueOf(i));
        gPhotoBM.add(i,inPhotoBM);
    }

    public static Bitmap getPhotoBM(int number){
        Log.v("@GVMget:", String.valueOf(number));
        return gPhotoBM.get(number);
    }

    public static int getgPhotoCount(){
        return gPhotoBM.size();
    }

}
