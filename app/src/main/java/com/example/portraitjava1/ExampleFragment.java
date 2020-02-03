package com.example.portraitjava1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.portraitjava1.ImageFreeShowerView;
import com.example.portraitjava1.ZoomView;

public class ExampleFragment extends Fragment {
    private final static String BACKGROUND_COLOR = "background_color";

//    public static ExampleFragment newInstance(@ColorRes int IdRes) {
    public static ExampleFragment newInstance(int position) {
        ExampleFragment frag = new ExampleFragment();
        Bundle b = new Bundle();
//        b.putInt(BACKGROUND_COLOR, IdRes);
        b.putInt("FragPosition", position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        ImageView imageView = view.findViewById(R.id.photoImage2);
        imageView.setImageBitmap(GlobalVariable.getPhotoBM(getArguments().getInt("FragPosition")));

        //こいつは最初からfragment的な処理をしてくれるな・・・
//        ImageFreeShowerView mImageFreeShowerViewMain = view.findViewById(R.id.photoImage2);
//        mImageFreeShowerViewMain.setBitmapList(GlobalVariable.getPhotoBM(getArguments().getInt("FragPosition")));

        return view;
    }
}