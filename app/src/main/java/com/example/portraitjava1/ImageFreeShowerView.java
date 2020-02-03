package com.example.portraitjava1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

public class ImageFreeShowerView extends View {

    private Context context;

    // 画像の配列
    private ArrayList<Bitmap> mBitmapList = new ArrayList<>();
    // 画像の配列においての今のインデックス
    private int mBitmapPosition = 0;
    // 画像の配列のインデックスの最小値
    private final int mPositionMin = 0;
    // 画像の配列のインデックスの最小値
    private int mPositionMax;

    // 画像切り替え後、初めてのonDrawかどうか
    private boolean isChangedFirstDraw = true;

    // タッチしたX座標
    private float touchPointX;
    // タッチしたY座標
    private float touchPointY;
    // 表示している画像の縮尺
    private float mLastScaleFactor = 1.0f;
    // 拡大したり移動したりするときにつかう「画像を変形させるための変数」
    private Matrix bitmapMatrix = new Matrix();
    // 切り替え時のバイブレーション
    private Vibrator mVibrator;
    // CanvasでつかうPaint変数
    private Paint mPaint = new Paint();
    // ピンチイン/アウト
    private ScaleGestureDetector mScaleGestureDetector;
    // ピンチイン/アウトしたときの動き
    private ScaleGestureDetector.SimpleOnScaleGestureListener mScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            // ピンチイン/アウト開始
            // タッチの座標を記録
            touchPointX = detector.getFocusX();
            touchPointY = detector.getFocusY();
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            // ピンチイン/アウト終了
            super.onScaleEnd(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // ピンチイン/アウト中(毎フレーム呼ばれる)
            // 縮尺を計算
            mLastScaleFactor = detector.getScaleFactor();
            // マトリックスに加算(縦と横を同じように拡大縮小する)
            bitmapMatrix.postScale(mLastScaleFactor, mLastScaleFactor, touchPointX, touchPointY);
            // Viewを再読み込み(onDrawの発火)
            invalidate();
            super.onScale(detector);
            return true;
        }
    };
    // タッチ、ホールド、ダブルタッチ等の動き
    private GestureDetector mGestureDetector;
    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        public boolean onScroll (MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // スクロールしたとき
            if (e1.getPointerId(0) == e2.getPointerId(0)) {
                // 開始地点と終了地点の指が同じ(一回も途切れなかった)なら
                // 画像を移動
                bitmapMatrix.postTranslate(-distanceX, -distanceY);
                // Viewを再読み込み(onDrawの発火)
                invalidate();
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDoubleTap (MotionEvent e) {
            // ダブルタッチしたとき
            if (e.getX() > ImageFreeShowerView.this.getWidth() / 2) {
                // 画面右側だったら画像を一個進める
                mBitmapPosition++;
                // 限界を超えたら一番前に戻る
                if (mBitmapPosition > mPositionMax) {mBitmapPosition = mPositionMin;}
            } else {
                // 画面左側だったら画像を一個戻す
                mBitmapPosition--;
                // 限界を下回ったら一番後ろにいく
                if (mBitmapPosition < mPositionMin) {mBitmapPosition = mPositionMax;}
            }
            // 画像交換後はじめてのonDraw
            isChangedFirstDraw = true;
            // 縮尺、移動をすべてリセット
            bitmapMatrix.reset();
            // Viewを再読み込み(onDrawの発火)
            invalidate();
            return super.onDoubleTap(e);
        }
    };

    // コンストラクタ
    public ImageFreeShowerView (Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ImageFreeShowerView (Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public ImageFreeShowerView (Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init () {
        // Gestureたちの設定
        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        mGestureDetector = new GestureDetector(context, mSimpleOnGestureListener);
        // バイブレーションの初期化
        mVibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        // すべてのGestureはここを通る
        return mGestureDetector.onTouchEvent(event) || mScaleGestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 画像切り替え後の処理
        if (isChangedFirstDraw){
            // 画面の横幅に画像の横を合わせるには何倍すればいいか
            float scaleX = ((float) getWidth()) / mBitmapList.get(mBitmapPosition).getWidth();
            // 画面の横幅に画像の横を合わせるには何倍すればいいか
            float scaleY = ((float) getHeight()) / mBitmapList.get(mBitmapPosition).getHeight();
            // 小さいほうを適応させる
            mLastScaleFactor = Math.min(scaleX, scaleY);
            bitmapMatrix.postScale(mLastScaleFactor, mLastScaleFactor, 0, 0);
            // バイブレーション
            mVibrator.vibrate(200);
            isChangedFirstDraw = false;
        }
        //すべてのmatrixを適応
        canvas.save();
        canvas.drawBitmap(mBitmapList.get(mBitmapPosition), bitmapMatrix, mPaint);
        canvas.restore();
    }

    public void setBitmapList(ArrayList<Bitmap> bitmapList) {
        // 外部から画像の配列を取り入れる。
        this.mBitmapList = bitmapList;
        // 最大値を初期化
        this.mPositionMax = bitmapList.size() - 1;
    }
}