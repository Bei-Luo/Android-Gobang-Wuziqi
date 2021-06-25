package com.example.myapplication.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.R;

/**
 * 游戏结束时的消息提示框
 */
public class Gametips extends Dialog implements View.OnClickListener {
    //声明xml文件里的组件
    private TextView tv_text;
    private Button bt_confirm, bt_cancel;

    private String title;

    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setConfirm(IOnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public void setCancel(IOnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public Gametips(@NonNull Context context) {
        super(context);
    }

    public Gametips(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置xml文件
        setContentView(R.layout.gametips);
        //设置界面大小
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int) (size.x * 0.8);//是dialog的宽度为app界面的80%
        getWindow().setAttributes(p);
        //初始化变量
        tv_text = (TextView) findViewById(R.id.tv_title);
        bt_confirm = (Button) findViewById(R.id.bt_confirm);
        bt_cancel = (Button) findViewById(R.id.bt_cancel);
        if (!TextUtils.isEmpty(title))
            tv_text.setText(title);
        //为按钮添加监听事件
        bt_confirm.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
    }

    /**
     * @param v 重写点击函数
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_confirm:
                if (confirmListener != null)
                    confirmListener.onConfirm(this);
                dismiss();
                break;
            case R.id.bt_cancel:
                if (cancelListener != null)
                    cancelListener.onCancel(this);
                dismiss();
                break;
        }
    }

    //设置点击监听的接口
    public interface IOnCancelListener {
        void onCancel(Gametips dialog);
    }

    public interface IOnConfirmListener {
        void onConfirm(Gametips dialog);
    }

}
