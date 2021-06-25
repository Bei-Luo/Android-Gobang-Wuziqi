package com.example.myapplication.two_game;

import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.ui.Gametips;

/**
 * 双游戏界面
 */
public class MainActivity2 extends AppCompatActivity implements CallBack {
    private twogame gobang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //消除上方框
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);
        initViews();
    }

    /**
     * 初始化函数
     */
    private void initViews() {
        //初始化五子棋棋盘
        gobang = (twogame) findViewById(R.id.wuziqi_view);
        gobang.setCallback(this);
    }

    /**
     * @param mode 0=平局，1=黑棋胜，2=白棋胜
     */
    @Override
    public void GameOver(int mode) {
        switch (mode) {
            case 0:
                gametips("双方和棋");
                break;
            case 1:
                gametips("黑棋胜利");
                break;
            case 2:
                gametips("白棋胜利");
                break;
        }
    }

    private void gametips(String title) {
        Gametips gametips = new Gametips(this);
        gametips.setCancel(new Gametips.IOnCancelListener() {
            @Override
            public void onCancel(Gametips dialog) {
                Toast.makeText(MainActivity2.this, "返回", Toast.LENGTH_SHORT).show();
            }
        });
        gametips.setConfirm(new Gametips.IOnConfirmListener() {
            @Override
            public void onConfirm(Gametips dialog) {
                gobang.resetGame();
                Toast.makeText(MainActivity2.this, "再来一局", Toast.LENGTH_SHORT).show();
            }
        });
        gametips.setTitle(title);
        gametips.show();
    }
}