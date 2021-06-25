package com.example.myapplication.single_game;

import com.example.myapplication.ui.Piece;

/**
 * com的回调
 */
public interface Com_CallBack {
    /**
     * com棋子回调
     * @param piece
     */
    void com_chess(Piece piece);
}
