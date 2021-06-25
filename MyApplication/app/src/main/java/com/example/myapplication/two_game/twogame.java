package com.example.myapplication.two_game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.myapplication.ui.Piece;
import com.example.myapplication.ui.checkerboardUI;

public class twogame extends checkerboardUI implements View.OnTouchListener {
    //回调
    protected CallBack callback;
    //先手标记
    protected int first_mark;
    //场上棋子总数
    protected int piece_sum;

    public twogame(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //设置触摸监听
        setOnTouchListener(this);
        //初始化先手标记
        first_mark = 1;
        //记录棋盘上的棋子总数用于和棋判定
        piece_sum = 0;
    }

    /**
     * 下棋
     *
     * @param downX
     * @param downY
     */
    protected void shezhiqizi(float downX, float downY) {
        int x = (((int) (downX / d * 2) + 1) / 2) - 1;
        int y = (((int) (downY / d * 2) + 1) / 2) - 1;
        //考虑越界
        if (x < 0 || y < 0 || x > 14 || y > 14)
            return;
        Piece p = new Piece(x, y);
        //考虑是否下过了
        if (board[x][y] != 0)
            return;
        piece_sum++;
        board[x][y] = first_mark;
        //重绘棋盘
        postInvalidate();
        //判断是否为和棋
        if (piece_sum == 225) {
            callback.GameOver(0);
        }
        //判断谁赢了
        if (new Piece(x, y).is_win(board, first_mark))
            callback.GameOver(first_mark);
        //黑棋白棋交错下
        first_mark = first_mark == 1 ? 2 : 1;
    }

    /**
     * 重现开始本局游戏
     */
    public void resetGame() {
        init();
        first_mark = 1;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置棋盘颜色
        canvas.drawColor(Color.parseColor("#FBE9B8"));
        //绘制棋盘
        depictUI(canvas);
    }

    /**
     * 获取用户手指触摸坐标
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float downX = event.getX();
                float downY = event.getY();
                shezhiqizi(downX, downY);
                //Log.v("wuziqi", String.valueOf(downX)+" "+String.valueOf(downY));
        }
        return false;
    }

    /**
     * 设置用于回调的变量
     *
     * @param callback
     */
    public void setCallback(CallBack callback) {
        this.callback = callback;
    }

}
