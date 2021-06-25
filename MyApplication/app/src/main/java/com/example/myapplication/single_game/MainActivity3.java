package com.example.myapplication.single_game;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.ui.Gametips;
import com.example.myapplication.ui.Piece;

/**
 * 单人游戏界面
 */
public class MainActivity3 extends AppCompatActivity implements View.OnClickListener, player_CallBack, Com_CallBack {
    //弹窗选择执子
    private PopupWindow chooseChess;
    //ui界面和玩家下棋操作
    private single_game_ui gobang;
    //com核心类
    private RobotCom robotCom;
    //棋盘
    private int board[][] = new int[15][15];
    //电脑执子颜色
    private int ComChessColor;
    //玩家执子颜色
    private int RivalChessColor;
    //场上棋子总数
    private int piece_sum;
    //界面宽
    private int width;
    //界面高
    private int height;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //消除上方框
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main3);
        initView();
        //创建一个监听 每当视图树发生改变时执行 用于获取整体界面的大小
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
            }
        });
    }

    private void initView() {
        gobang = (single_game_ui) findViewById(R.id.AIUI);
        //设置回调
        gobang.setCallback(this);
        //共享棋盘
        gobang.setBoard(board);
        //创建com类
        robotCom = new RobotCom(this, board);
        //初始化棋子总数用于和棋判定
        piece_sum = 0;
    }

    /**
     * 弹出对话框选择执子
     */
    private void initPop() {
        //创建弹出选择执子
        View view = View.inflate(this, R.layout.chess_color_select, null);
        //绑定监听
        ImageButton white = (ImageButton) view.findViewById(R.id.imageButton1);
        ImageButton black = (ImageButton) view.findViewById(R.id.imageButton2);
        white.setOnClickListener(this);
        black.setOnClickListener(this);
        chooseChess = new PopupWindow(view, width, height);
        chooseChess.showAsDropDown(gobang, Gravity.CENTER, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //玩家先手
            case R.id.imageButton1:
                //设置颜色信息
                ComChessColor = 2;
                RivalChessColor = 1;
                gobang.setColor(RivalChessColor);
                robotCom.setColor(ComChessColor);
                //打开触摸 等待玩家下棋
                gobang.setTouch_mark(true);
                //销毁执子选择框
                chooseChess.dismiss();
                break;
            //玩家后手
            case R.id.imageButton2:
                //设置颜色信息
                ComChessColor = 1;
                RivalChessColor = 2;
                gobang.setColor(RivalChessColor);
                robotCom.setColor(ComChessColor);
                //关闭触摸
                gobang.setTouch_mark(false);
                //com第一次下棋
                robotCom.StartOneChess();
                //销毁执子选择框
                chooseChess.dismiss();
                break;
        }
    }


    private void GameOver(int mode) {
        if (mode == 0) {
            gametips("双方和棋");
        } else if (mode == ComChessColor) {
            gametips("电脑胜利");
        } else {
            gametips("玩家获胜");
        }
    }

    private void gametips(String title) {
        Gametips gametips = new Gametips(this);
        gametips.setCancel(new Gametips.IOnCancelListener() {
            @Override
            public void onCancel(Gametips dialog) {
                Toast.makeText(MainActivity3.this, "返回", Toast.LENGTH_SHORT).show();
            }
        });
        gametips.setConfirm(new Gametips.IOnConfirmListener() {
            @Override
            public void onConfirm(Gametips dialog) {
                for (int i = 0; i < board.length; i++)
                    for (int j = 0; j < board[i].length; ++j)
                        board[i][j] = 0;
                gobang.postInvalidate();
                initPop();
                Toast.makeText(MainActivity3.this, "再来一局", Toast.LENGTH_SHORT).show();
            }
        });
        gametips.setTitle(title);
        gametips.show();
    }

    /**
     * 玩家下棋的回调
     *
     * @param piece
     */
    @Override
    public void player_chess(Piece piece) {
        //将触摸关闭
        gobang.setTouch_mark(false);
        piece_sum++;
        //判断胜负
        if (piece.is_win(board, RivalChessColor)) {
            GameOver(RivalChessColor);
            return;
        }
        if (piece_sum == 255) {
            GameOver(0);
            return;
        }
        //com下棋
        robotCom.com_start();
    }

    /**
     * com下棋的回调
     *
     * @param piece
     */
    @Override
    public void com_chess(Piece piece) {
        gobang.postInvalidate();
        piece_sum++;
        //判断胜负
        if (piece.is_win(board, ComChessColor)) {
            GameOver(ComChessColor);
            return;
        }
        if (piece_sum == 255) {
            GameOver(0);
            return;
        }
        //将触摸打开
        gobang.setTouch_mark(true);
    }
}