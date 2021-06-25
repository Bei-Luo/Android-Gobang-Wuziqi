package com.example.myapplication.ui;

/**
 * 存放棋子信息的类
 */
public class Piece {
    public int x;
    public int y;

    //public int piece=0; // 0代表无棋子，1代表黑子，2代表白子
    public Piece() {
        this.x = 0;
        this.y = 0;
    }

    public Piece(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean is_win(int board[][], int color) {
        int count = 0;
        int i, j, k;
        int xx, yy;
        int[][] mode = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        for (i = 0; i < 4; ++i) {
            count = 1;
            for (j = -1; j <= 1; j += 2)
                for (k = 1; k < 5; ++k) {
                    xx = x + (mode[i][0] * j * k);
                    yy = y + (mode[i][1] * j * k);
                    if (xx < 0 || yy < 0 || xx > 14 || yy > 14)
                        break;
                    if (board[xx][yy] == color)
                        count++;
                    else
                        break;
                }
            if (count == 5)
                return true;
        }
        return false;
    }

}
