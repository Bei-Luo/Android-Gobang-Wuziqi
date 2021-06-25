package com.example.myapplication.online_game;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.tcponline;
import com.example.myapplication.ui.Piece;

public class MainActivity4 extends AppCompatActivity implements View.OnClickListener, rival_CallBack, self_CallBack {
    private PopupWindow matching;
    private online_game_ui gobang;
    private int width;
    private int height;
    private tcponline client;
    int board[][] = new int[15][15];
    private int SelfChessColor;
    private int RivalChessColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //消除上方框
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main4);
        client = (tcponline) this.getApplication();
        initView();
        //创建一个监听每当视图树发生改变时执行 用于获取整体界面的大小
        gobang.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //为了防止多次触发 注销监听
                gobang.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //通过DisplayMetrics来获取界面大小
                DisplayMetrics dm = getResources().getDisplayMetrics();
                width = dm.widthPixels;
                height = dm.heightPixels;
                initPop();
                initserver();
            }
        });
    }

    private void initserver() {
        JSONObject object = new JSONObject();
        object.put("function", "match");
        client.Msg_send(object.toString());
    }

    private void initView() {
        gobang = (online_game_ui) findViewById(R.id.onlineUI);
        gobang.setCallback(this);
        //共享棋盘
        gobang.setBoard(board);
        gobang.setTouch_mark(false);

        client.callback = this;

    }

    private void initPop() {
        //创建等待框
        View view = View.inflate(this, R.layout.ing, null);
        //绑定监听
        Button cancel = (Button) view.findViewById(R.id.button);
        cancel.setOnClickListener(this);
        matching = new PopupWindow(view, width, height);
        matching.showAsDropDown(gobang, Gravity.CENTER, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                startActivity(new Intent(this, MainActivity.class));
                finish();
        }
    }


    @Override
    public void init_chess(JSONObject object) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                matching.dismiss();
            }
        });
        if (object.getInteger("color") == 1) {
            gobang.setTouch_mark(true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity4.this, "你是先手", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity4.this, "你是后手", Toast.LENGTH_SHORT).show();
                }
            });
        }
        SelfChessColor = object.getInteger("color");
        RivalChessColor = object.getInteger("color") == 1 ? 2 : 1;
        gobang.setColor(SelfChessColor);
    }

    @Override
    public void rival_chess(int x, int y) {
        board[x][y] = RivalChessColor;
        gobang.postInvalidate();
        gobang.setTouch_mark(true);
    }

    @Override
    public void gameover(JSONObject object) {
        if (object.getInteger("color") == SelfChessColor)
            Toast.makeText(this, "你赢了", Toast.LENGTH_SHORT).show();
        else {
            int x = object.getInteger("x");
            int y = object.getInteger("y");
            board[x][y] = RivalChessColor;
            gobang.postInvalidate();
            Toast.makeText(this, "你输了", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void self_chess(Piece piece) {
        gobang.setTouch_mark(false);
        JSONObject object = new JSONObject();
        object.put("x", piece.x);
        object.put("y", piece.y);
        object.put("function", "xiaqi");
        client.Msg_send(object.toString());
    }
}