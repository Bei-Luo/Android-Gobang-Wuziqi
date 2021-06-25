package com.example.myapplication.single_game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.myapplication.ui.Piece;
import com.example.myapplication.ui.checkerboardUI;

import java.util.ArrayList;

public class single_game_ui extends checkerboardUI implements View.OnTouchListener {
    //触摸标记
    private boolean touch_mark;
    //下棋顺序
    ArrayList<Piece> place_queue = new ArrayList<>(225);
    //回调
    protected player_CallBack callback;
    //玩家执子颜色
    private int RivalChessColor;

    public single_game_ui(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //注册触摸监听
        setOnTouchListener(this);
    }

    public void setColor(int color) {
        RivalChessColor = color;
    }

    public void setTouch_mark(boolean touch_mark) {
        this.touch_mark = touch_mark;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置棋盘颜色
        canvas.drawColor(Color.parseColor("#FBE9B8"));
        //绘制棋盘
        depictUI(canvas);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!touch_mark)
            return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float downX = event.getX();
                float downY = event.getY();
                shezhiqizi(downX, downY);
        }
        return false;
    }

    /**
     * 玩家下棋
     *
     * @param downX
     * @param downY
     */
    private void shezhiqizi(float downX, float downY) {
        //通过触摸的坐标计算出棋盘坐标
        int x = (((int) (downX / d * 2) + 1) / 2) - 1;
        int y = (((int) (downY / d * 2) + 1) / 2) - 1;
        //考虑越界
        if (x < 0 || y < 0 || x > 14 || y > 14)
            return;
        //考虑是否下过了
        if (board[x][y] != 0)
            return;
        //下子
        board[x][y] = RivalChessColor;
        //重绘棋盘
        postInvalidate();
        //判断是否为和棋
        //回调
        callback.player_chess(new Piece(x, y));
    }

    /**
     * 设置用于回调的变量
     *
     * @param callback
     */
    public void setCallback(player_CallBack callback) {
        this.callback = callback;
    }

    public void setBoard(int board[][]) {
        this.board = board;
    }
}
