package com.example.myapplication.online_game;

import com.alibaba.fastjson.JSONObject;

/**
 * 联机玩家的棋子回调（对方）
 */
public interface rival_CallBack {
    void init_chess(JSONObject object);

    void rival_chess(int x, int y);

    void gameover(JSONObject object);
}
