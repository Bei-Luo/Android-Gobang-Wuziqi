package com.example.myapplication.single_game;

import com.example.myapplication.ui.Piece;

/**
 * 玩家回调函数
 */
public interface player_CallBack {
    /**
     * 玩家的棋子回调
     */
    void player_chess(Piece piece);
}
