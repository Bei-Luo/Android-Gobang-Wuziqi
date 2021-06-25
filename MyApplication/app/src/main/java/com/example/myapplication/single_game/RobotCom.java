package com.example.myapplication.single_game;

import android.os.Looper;

import com.example.myapplication.ui.Piece;

import java.util.ArrayList;
import java.util.Random;

/**
 * com算法核心类
 */
public class RobotCom implements Runnable {
    //棋盘
    private int board[][] = new int[15][15];
    //暂存棋盘
    private int gBoard[][] = new int[15][15];
    //电脑执子颜色
    private int ComChessColor;
    //玩家执子颜色
    private int RivalChessColor;
    //回调
    protected Com_CallBack com_callBack;

    private final int M_SIZE = 15;
    //计分板
    private final int WIN5 = 100000;//连5
    private final int ALIVE4 = 10000;//活4
    private final int ALIVE3 = 1000;//活3
    private final int DIE4 = 1000;//冲4
    private final int ALIVE2 = 100;//活2
    private final int DIE3 = 100;//冲3
    private final int DIE2 = 10;//冲2
    private final int ALIVE1 = 10;//活1

    /**
     * 构造函数 设置回调 传入棋盘
     */
    public RobotCom(Com_CallBack com_callBack, int[][] board) {
        this.com_callBack = com_callBack;
        this.board = board;
    }

    public void setColor(int color) {
        ComChessColor = color;
        RivalChessColor = (color == 1) ? 2 : 1;
    }

    public void StartOneChess() {
        //在以7，7为中心半径为三的范围内随机落子
        Random r = new Random();
        int x = r.nextInt(3) + 6;
        int y = r.nextInt(3) + 6;
        board[x][y] = ComChessColor;
        com_callBack.com_chess(new Piece(x, y));
    }

    /**
     * com开始下棋
     */
    public void com_start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        Looper.prepare();
        Piece p = new Piece();
        alphabeta(4, board, p);
        //回调下棋位置
        board[p.x][p.y] = ComChessColor;
        com_callBack.com_chess(p);
        Looper.loop();
    }

    /**
     * 判断是否有s格内是否有棋子
     *
     * @param p
     * @return
     */
    private boolean has_neighbors(Piece p) {
        //设定判断距离
        int s = 2;
        //防止溢出
        for (int i = (p.x - s > 0 ? p.x - s : 0); i <= p.x + s && i < 15; i++)
            for (int j = (p.y - s > 0 ? p.y - s : 0); j <= p.y + s && j < 15; j++)
                if (0 != gBoard[i][j])
                    return true;
        return false;
    }

    /**
     * 通过计分板来打分
     *
     * @param number 连子的个数
     * @param empty  两端为空的个数
     * @return
     */
    private int score_table(int number, int empty) {
        if (number >= 5)
            return WIN5;
        else if (number == 4) {
            if (empty == 2)
                return ALIVE4;
            else if (empty == 1)
                return DIE4;
        } else if (number == 3) {
            if (empty == 2)
                return ALIVE3;
            else if (empty == 1)
                return DIE3;
        } else if (number == 2) {
            if (empty == 2)
                return ALIVE2;
            else if (empty == 1)
                return DIE2;
        } else if (number == 1 && empty == 2)
            return ALIVE1;
        return 0;
    }

    /**
     * 单点评估函数
     *
     * @param p          位置
     * @param chessColor 评价颜色
     * @return 得分
     */
    private int evaluate_point(Piece p, int chessColor) {
        int score = 0;
        int number, empty;
        int i, j;
        int x = p.x;
        int y = p.y;
        //横
        number = 0;
        empty = 0;
        for (i = x - 1; i >= 0 && i > x - 5; i--) {
            if (chessColor == gBoard[i][y])
                number++;
            else if (0 == gBoard[i][y]) {
                empty++;
                break;
            } else
                break;
        }
        for (i = x + 1; i < 15 && i < x + 5; i++) {
            if (chessColor == gBoard[i][y])
                number++;
            else if (0 == gBoard[i][y]) {
                empty++;
                break;
            } else
                break;
        }
        score += score_table(number + 1, empty);
        //竖
        number = 0;
        empty = 0;
        for (j = y - 1; j >= 0 && j > y - 5; j--) {
            if (chessColor == gBoard[x][j])
                number++;
            else if (0 == gBoard[x][j]) {
                empty++;
                break;
            } else
                break;
        }
        for (j = y + 1; j < 15 && j < y + 5; j++) {
            if (chessColor == gBoard[x][j])
                number++;
            else if (0 == gBoard[x][j]) {
                empty++;
                break;
            } else
                break;
        }
        score += score_table(number + 1, empty);
        //主对角线
        number = 0;
        empty = 0;
        for (i = x - 1, j = y - 1; i >= 0 && j >= 0 && i > x - 5 && j > y - 5; i--, j--) {
            if (chessColor == gBoard[i][j])
                number++;
            else if (0 == gBoard[i][j]) {
                empty++;
                break;
            } else
                break;
        }
        for (i = x + 1, j = y + 1; i < 15 && j < 15 && i < x + 5 && j < y + 5; i++, j++) {
            if (chessColor == gBoard[i][j])
                number++;
            else if (0 == gBoard[i][j]) {
                empty++;
                break;
            } else
                break;
        }
        score += score_table(number + 1, empty);
        //副对角线
        number = 0;
        empty = 0;
        for (i = x - 1, j = y + 1; i >= 0 && j < 15 && i > x - 5 && j < y + 5; i--, j++) {
            if (chessColor == gBoard[i][j])
                number++;
            else if (0 == gBoard[i][j]) {
                empty++;
                break;
            } else
                break;
        }
        for (i = x + 1, j = y - 1; i < 15 && j >= 0 && i < x + 5 && j > y - 5; i++, j--) {
            if (chessColor == gBoard[i][j])
                number++;
            else if (0 == gBoard[i][j]) {
                empty++;
                break;
            } else
                break;
        }
        score += score_table(number + 1, empty);
        return score;

    }

    /**
     * 全局评估函数
     *
     * @return
     */
    private int evaluate_situation() {
        int ComScore = 0;
        int RivalScore = 0;
        int i, j;
        int x, y;
        ArrayList<Integer> n = new ArrayList<>(M_SIZE + 1);
        //横
        for (i = 0; i < M_SIZE; i++) {
            for (j = 0; j < M_SIZE; j++)
                n.add(gBoard[j][i]);
            ComScore += count_score(n, ComChessColor);
            RivalScore += count_score(n, RivalChessColor);
            n.clear();
        }
        //竖
        for (i = 0; i < M_SIZE; i++) {
            for (j = 0; j < M_SIZE; j++)
                n.add(gBoard[i][j]);
            ComScore += count_score(n, ComChessColor);
            RivalScore += count_score(n, RivalChessColor);
            n.clear();
        }
        //主对角线
        for (i = 0; i < 15; i++) {
            for (x = i, y = 0; x < 15 && y < 15; x++, y++)
                n.add(gBoard[x][y]);
            ComScore += count_score(n, ComChessColor);
            RivalScore += count_score(n, RivalChessColor);
            n.clear();
        }
        for (i = 1; i < 15; i++) {
            for (x = 0, y = i; x < 15 && y < 15; x++, y++)
                n.add(gBoard[x][y]);
            ComScore += count_score(n, ComChessColor);
            RivalScore += count_score(n, RivalChessColor);
            n.clear();
        }
        //副对角线
        for (i = 0; i < 15; i++) {
            for (x = 0, y = i; x < 15 && y >= 0; x++, y--)
                n.add(gBoard[x][y]);
            ComScore += count_score(n, ComChessColor);
            RivalScore += count_score(n, RivalChessColor);
            n.clear();
        }
        for (i = 1; i < 15; i++) {
            for (x = 14, y = i; x >= 0 && y < 15; x--, y++)
                n.add(gBoard[x][y]);
            ComScore += count_score(n, ComChessColor);
            RivalScore += count_score(n, RivalChessColor);
            n.clear();
        }
        return ComScore - RivalScore;
    }

    /**
     * 评估一维数组的得分
     *
     * @param n
     * @param chessColor
     * @return
     */
    private int count_score(ArrayList<Integer> n, int chessColor) {
        int empty = 0;
        int number = 0;
        int scoretmp = 0;
        if (0 == n.get(0))
            empty++;
        else if (chessColor == n.get(0))
            number++;
        for (Integer i : n) {
            if (chessColor == i)
                number++;
            else if (0 == i) {
                if (number == 0)
                    empty = 1;
                else {
                    scoretmp += score_table(number, empty + 1);
                    empty = 1;
                    number = 0;
                }
            } else {
                scoretmp += score_table(number, empty);
                empty = 0;
                number = 0;
            }
        }
        scoretmp += score_table(number, empty);
        return scoretmp;
    }

    /**
     * 启发式搜索
     *
     * @return
     */
    private ArrayList<Piece> generate_point() {
        ArrayList<Piece> RivalFive = new ArrayList<Piece>();
        ArrayList<Piece> ComFour = new ArrayList<Piece>();
        ArrayList<Piece> RivalFour = new ArrayList<Piece>();
        ArrayList<Piece> ComDoubleThree = new ArrayList<Piece>();
        ArrayList<Piece> RivalDoubleThree = new ArrayList<Piece>();
        ArrayList<Piece> ComThree = new ArrayList<Piece>();
        ArrayList<Piece> RivalThree = new ArrayList<Piece>();
        ArrayList<Piece> ComTwo = new ArrayList<Piece>();
        ArrayList<Piece> RivalTwo = new ArrayList<Piece>();
        ArrayList<Piece> others = new ArrayList<Piece>();

        ArrayList<Piece> queue = new ArrayList<Piece>();
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                Piece p = new Piece(i, j);
                if ((0 == gBoard[i][j]) && (has_neighbors(p))) {
                    int ComScore = evaluate_point(p, ComChessColor);
                    int RivalScore = evaluate_point(p, RivalChessColor);
                    if (ComScore >= WIN5) {
                        queue.add(p);
                        return queue;
                    } else if (RivalScore >= WIN5)
                        RivalFive.add(p);
                    else if (ComScore >= ALIVE4)
                        ComFour.add(p);
                    else if (RivalScore >= ALIVE4)
                        RivalFour.add(p);
                    else if (ComScore >= ALIVE3 * 2)
                        ComDoubleThree.add(p);
                    else if (RivalScore >= ALIVE3 * 2)
                        RivalDoubleThree.add(p);
                    else if (ComScore >= ALIVE3)
                        ComThree.add(p);
                    else if (RivalScore >= ALIVE3)
                        RivalThree.add(p);
                    else if (ComScore >= ALIVE2)
                        ComTwo.add(p);
                    else if (RivalScore >= ALIVE2)
                        RivalTwo.add(p);
                    else
                        others.add(p);
                }
            }
        }
        if (RivalFive.size() > 0) {
            queue = RivalFive;
            return queue;
        }
        if (ComFour.size() > 0 || RivalFour.size() > 0) {
            queue = ComFour;
            queue.addAll(RivalFour);
            return queue;
        }
        if (ComDoubleThree.size() > 0 || RivalDoubleThree.size() > 0) {
            queue = ComDoubleThree;
            queue.addAll(RivalDoubleThree);
            return queue;
        }

        queue = ComThree;
        queue.addAll(RivalThree);
        queue.addAll(ComTwo);
        queue.addAll(RivalTwo);
        return queue;
    }

    /**
     * 搜索函数
     */
    private int alphabeta(int depth, int board[][], Piece p) {
        int best = -WIN5;
        ArrayList<Piece> option_queue = new ArrayList<Piece>(100);  //待选的空子队列
        ArrayList<Piece> sure_queue = new ArrayList<Piece>();        //最合适的下子位置
        //更新棋盘
        for (int i = 0; i < 15; i++)
            for (int j = 0; j < 15; j++)
                gBoard[i][j] = board[i][j];
        option_queue = generate_point();
        //System.out.println(option_queue.size());
        for (Piece i : option_queue) {
            int x = i.x;
            int y = i.y;
            gBoard[x][y] = ComChessColor;
            int tmp = min_alphabeta(depth - 1, i, -WIN5, WIN5);
            if (tmp == best) {
                sure_queue.add(i);
            }
            if (tmp > best) {
                best = tmp;
                sure_queue.clear();
                sure_queue.add(i);
            }
            gBoard[x][y] = 0;
        }
        //如果有多个解 取随机
        Random random = new Random();
        int k = random.nextInt(sure_queue.size());
        p.x = sure_queue.get(k).x;
        p.y = sure_queue.get(k).y;
        return best;
    }

    /**
     * alpha剪枝
     *
     * @param depth
     * @param alpha
     * @param beta
     * @return
     */
    private int min_alphabeta(int depth, Piece p, int alpha, int beta) {
        int res = evaluate_situation();
        //代表临时beta
        int best = WIN5;
        ArrayList<Piece> v = new ArrayList<>(50);
        //判断是否胜利
        if (depth <= 0 || p.is_win(gBoard, RivalChessColor))
            return res;
        //生成待搜索序列
        v = generate_point();
        for (Piece i : v) {
            int x = i.x;
            int y = i.y;
            gBoard[x][y] = RivalChessColor;
            int tmp = max_alphabeta(depth - 1, p, alpha, Math.min(best, beta));
            gBoard[x][y] = 0;
            best = Math.min(tmp, best);
            if (tmp < alpha)
                break;
        }
        return best;
    }

    /**
     * beta剪枝
     *
     * @param depth
     * @param alpha
     * @param beta
     * @return
     */
    private int max_alphabeta(int depth, Piece p, int alpha, int beta) {
        int res = evaluate_situation();
        //代表临时alpha
        int best = -WIN5;
        ArrayList<Piece> v = new ArrayList<>(50);
        //判断是否胜利
        if (depth <= 0 || p.is_win(gBoard, ComChessColor))
            return res;
        //生成待搜索序列
        v = generate_point();
        for (Piece i : v) {
            int x = i.x;
            int y = i.y;
            gBoard[x][y] = ComChessColor;
            int tmp = min_alphabeta(depth - 1, p, Math.max(best, alpha), beta);
            gBoard[x][y] = 0;
            best = Math.max(tmp, best);
            if (tmp > beta)
                break;
        }
        return best;
    }

}
