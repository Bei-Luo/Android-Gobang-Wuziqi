package com.example.myapplication.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.myapplication.R;

/**
 * 用于绘制整个棋盘
 */
public class checkerboardUI extends View {
    //画笔
    protected Paint paint;
    //格间距
    protected int d;
    //中心点坐标
    protected int centerx, centery;
    //棋盘坐标信息映射
    protected Piece_map[][] arv = new Piece_map[15][15];
    //棋盘数组
    protected int board[][] = new int[15][15];
    //bitmap
    protected Bitmap whiteChess;
    protected Bitmap blackChess;
    //Rect
    protected Rect rect;

    public checkerboardUI(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, paint);
        //初始化画笔
        paint = new Paint();
        //初始化rect
        rect = new Rect();
        //设置抗锯齿
        paint.setAntiAlias(true);
        //设置画笔颜色
        paint.setColor(Color.parseColor("#BEA67B"));
        //初始化图片bitmap
        blackChess = BitmapFactory.decodeResource(context.getResources(), R.drawable.black_chess);
        whiteChess = BitmapFactory.decodeResource(context.getResources(), R.drawable.white_chess);
    }

    /**
     * 创建坐标点与棋盘格子的映射关系.
     */
    public void init() {
        int startx = centerx - 7 * d, starty = centery - 7 * d;
        for (int i = 0; i < 15; ++i)
            for (int j = 0; j < 15; ++j) {
                arv[i][j] = new Piece_map(starty + i * d, startx + j * d);
                board[i][j] = 0;
            }
    }

    /**
     * 绘制棋盘UI.
     *
     * @param canvas
     */
    protected void depictUI(Canvas canvas) {
        int margin = d / 2 - 4;
        Piece_map x1, x2, y1, y2, temp;
        //绘制棋盘
        for (int i = 0; i < 15; ++i) {
            x1 = arv[i][0];
            x2 = arv[i][14];
            y1 = arv[0][i];
            y2 = arv[14][i];
            canvas.drawLine(x1.x, x1.y, x2.x, x2.y, paint);  //绘制横线
            canvas.drawLine(y1.x, y1.y, y2.x, y2.y, paint);  //绘制竖线
        }
        temp = arv[3][3];
        canvas.drawCircle(temp.x, temp.y, 6, paint);
        temp = arv[11][11];
        canvas.drawCircle(temp.x, temp.y, 6, paint);
        temp = arv[3][11];
        canvas.drawCircle(temp.x, temp.y, 6, paint);
        temp = arv[11][3];
        canvas.drawCircle(temp.x, temp.y, 6, paint);
        temp = arv[7][7];
        canvas.drawCircle(temp.x, temp.y, 6, paint);
        //绘制棋子
        for (int i = 0; i < 15; ++i)
            for (int j = 0; j < 15; ++j) {
                temp = arv[i][j];
                rect.set(temp.x - margin, temp.y - margin, temp.x + margin, temp.y + margin);
                switch (board[i][j]) {
                    case 0:
                        break;
                    case 1:
                        canvas.drawBitmap(blackChess, null, rect, paint);
                        break;
                    case 2:
                        canvas.drawBitmap(whiteChess, null, rect, paint);
                        break;
                }
            }
    }

    /**
     * 获取界面尺寸
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //除以16是因为要预留一圈边界，所以要多画
        d = w / 16;
        //计算中心点坐标
        centerx = w / 2;
        centery = h / 2;
        init();
        Log.v("wuziqi", String.valueOf(d));
        //Log.v("wuziqi",w+" "+h+" "+oldw+" "+oldh);
        //Log.v("wuziqi", String.valueOf(getRight()-getLeft()));
    }

    /**
     * 重新测量棋盘 并设定大小，
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        //获取宽高中较小的值
        int len = Math.min(width, height);
        //重新设置宽高
        //Log.v("onMeasure", String.valueOf(len)+" "+String.valueOf(height));
        setMeasuredDimension(len, len);
    }
}

