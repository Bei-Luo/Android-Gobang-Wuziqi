package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.online_game.MainActivity4;
import com.example.myapplication.single_game.MainActivity3;
import com.example.myapplication.two_game.MainActivity2;

/**
 * 主菜单界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button1, button2, button3, button4, login_button, button_confirm, button_cancel, registered_button;
    private int width;
    private int height;
    private PopupWindow loging_pop;
    private PopupWindow registered_pop;
    EditText user;
    EditText pass;
    TextView name_text;
    EditText pass_repeat;
    tcponline client;
    boolean server_flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        init();
        button1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //为了防止多次触发 注销监听
                button1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //通过DisplayMetrics来获取界面大小
                DisplayMetrics dm = getResources().getDisplayMetrics();
                width = dm.widthPixels;
                height = dm.heightPixels;
            }
        });
    }

    private void init() {
        name_text = (TextView) findViewById(R.id.name_text);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        login_button = (Button) findViewById(R.id.login_button);
        registered_button = (Button) findViewById(R.id.registered_button);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        login_button.setOnClickListener(this);
        registered_button.setOnClickListener(this);

        client =  (tcponline) this.getApplication();
        if (client.Connect_start()) {
            Toast.makeText(MainActivity.this, "服务器连接成功", Toast.LENGTH_SHORT).show();
            server_flag = true;
        } else
            Toast.makeText(MainActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();

    }

    public void onClick(View v) {
        JSONObject object = new JSONObject();
        switch (v.getId()) {
            case R.id.button1:
                startActivity(new Intent(this, MainActivity3.class));
                break;
            case R.id.button2:
                startActivity(new Intent(this, MainActivity2.class));
                break;
            case R.id.button3:
                if (!server_flag) {
                    server_error();
                    break;
                }
                startActivity(new Intent(this, MainActivity4.class));
                break;
            case R.id.button4:
                break;
            case R.id.login_button:
                initPop_login();
                break;
            case R.id.registered_button:
                initPop_registered();
                break;
            case R.id.login_confirm:
                login();
                break;
            case R.id.registered_confirm:
                registered();
                break;
            case R.id.login_cancel:
                loging_pop.dismiss();
                break;
            case R.id.registered_cancel:
                registered_pop.dismiss();
                break;
        }
    }

    private void server_error() {
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(MainActivity.this);
        alterDiaglog.setTitle("服务器连接失败");
        alterDiaglog.setMessage("无法进行联机对战");
        alterDiaglog.setPositiveButton("确定", null);
        alterDiaglog.show();
    }

    private void registered() {
        String str_login = user.getText().toString();
        String str_password = pass.getText().toString();
        String str_password_repeat = pass_repeat.getText().toString();
        if (!str_password_repeat.equals(str_password))
            Toast.makeText(this, "两次密码输入错误", Toast.LENGTH_SHORT).show();
        else {
            JSONObject object = new JSONObject();
            object.put("function", "registered");
            object.put("username", str_login);
            object.put("password", str_password);
            object = JSONObject.parseObject(client.Msg_return(object.toString()));
            if (object.getBoolean("return")) {
                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                registered_pop.dismiss();
            } else {
                Toast.makeText(this, "注册失败,用户名已存在", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void login() {
        String str_user = user.getText().toString();
        String str_password = pass.getText().toString();
        JSONObject object = new JSONObject();
        object.put("function", "login");
        object.put("username", str_user);
        object.put("password", str_password);
        object = JSONObject.parseObject(client.Msg_return(object.toString()));
        if (object.getBoolean("return")) {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            loging_pop.dismiss();
            name_text.setText(str_user);
        } else {
            if (object.getInteger("error") == 1)
                Toast.makeText(this, "登陆失败,用户名不存在", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "登录失败,密码错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void initPop_login() {
        //绑定xml文件
        View view = View.inflate(this, R.layout.login, null);
        //绑定
        user = (EditText) view.findViewById(R.id.login_useredit);
        pass = (EditText) view.findViewById(R.id.login_passwardedit);

        button_confirm = (Button) view.findViewById(R.id.login_confirm);
        button_confirm.setOnClickListener(this);
        button_cancel = (Button) view.findViewById(R.id.login_cancel);
        button_cancel.setOnClickListener(this);
        loging_pop = new PopupWindow(view, width, height);
        //获取焦点
        loging_pop.setFocusable(true);
        View rootview = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
        loging_pop.showAsDropDown(rootview, Gravity.CENTER, 0, 0);

    }

    private void initPop_registered() {
        //绑定xml文件
        View view = View.inflate(this, R.layout.registered, null);
        //绑定
        user = (EditText) view.findViewById(R.id.registered_useredit);
        pass = (EditText) view.findViewById(R.id.registered_passwardedit);
        pass_repeat = (EditText) view.findViewById(R.id.registered_passwardedit_repeat);

        button_confirm = (Button) view.findViewById(R.id.registered_confirm);
        button_confirm.setOnClickListener(this);
        button_cancel = (Button) view.findViewById(R.id.registered_cancel);
        button_cancel.setOnClickListener(this);
        registered_pop = new PopupWindow(view, width, height);
        //获取焦点
        registered_pop.setFocusable(true);
        View rootview = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
        registered_pop.showAsDropDown(rootview, Gravity.CENTER, 0, 0);

    }


}




