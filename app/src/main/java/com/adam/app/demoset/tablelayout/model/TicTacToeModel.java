/*
 * Copyright (c) 2026 Adam Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.adam.app.demoset.tablelayout.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicTacToeModel {

    // constants
    public static final char EMPTY = '\0';
    public static final char PLAYER = 'X';
    public static final char COMPUTER = 'O';
    // win pattern
    private static final int[][] WIN_PATTERN = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},  // horizontal
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},  // vertical
            {0, 4, 8}, {2, 4, 6}              // diagonal
    };
    // game board
    private final char[] mBoard = new char[9];
    // cell wins
    private final List<Integer> mCellWins = new ArrayList<>();

    /**
     * constructor
     */


    public TicTacToeModel() {
        reset();
    }

    /**
     * reset the board
     */
    public void reset() {
        Arrays.fill(mBoard, EMPTY);
        mCellWins.clear();
    }

    /**
     * get the cell
     *
     * @param index the index of the cell
     * @return data of the cell
     */
    public char getCell(int index) {
        return mBoard[index];
    }

    /**
     * set the cell
     *
     * @param index the index of the cell
     * @param mark  the mark of the cell
     * @return true if the cell is set, false otherwise
     */
    public boolean setCell(int index, char mark) {
        if (mBoard[index] == EMPTY) {
            mBoard[index] = mark;
            return true;
        }
        return false;
    }

    /**
     * check if the cell is full
     *
     * @return true if the cell is full, false otherwise
     */
    public boolean isFull() {
        for (char c : mBoard) {
            if (c == EMPTY) {
                return false;
            }
        }
        return true;
    }

    /**
     * check if the cell is empty
     *
     * @param index the index of the cell
     * @return true if the cell is empty, false otherwise
     */
    public boolean isEmpty(int index) {
        return mBoard[index] == EMPTY;
    }

    /**
     * get the board
     *
     * @return the board
     */
    public char[] getBoard() {
        return mBoard;
    }

    /**
     * get the cell wins
     *
     * @return the cell wins
     */
    public List<Integer> getCellWins() {
        return mCellWins;
    }


    /**
     * check if the player wins
     *
     * @param mark the mark of the player
     * @return true if the player wins, false otherwise
     */
    public boolean checkWin(char mark) {
        // check
        for (int[] pattern : WIN_PATTERN) {
            if (mBoard[pattern[0]] == mark && mBoard[pattern[1]] == mark && mBoard[pattern[2]] == mark) {
                mCellWins.add(pattern[0]);
                mCellWins.add(pattern[1]);
                mCellWins.add(pattern[2]);
                return true;
            }
        }

        return false;
    }

    /**
     * check if the computer wins
     *
     * @param player the mark of the player
     * @return the index of the cell which will win the game
     */
    public int findWinningMove(char player) {
        for (int[] pattern : WIN_PATTERN) {
            int a = pattern[0];
            int b = pattern[1];
            int c = pattern[2];
            // check if the move is possible
            if (mBoard[a] == EMPTY && mBoard[b] == player && mBoard[c] == player) {
                return a;
            }
            if (mBoard[b] == EMPTY && mBoard[a] == player && mBoard[c] == player) {
                return b;
            }
            if (mBoard[c] == EMPTY && mBoard[a] == player && mBoard[b] == player) {
                return c;
            }
        }

        return -1;
    }

}
