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

package com.adam.app.demoset.tablelayout.domain.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TicTacToe game logic entity.
 */
public class TicTacToeGame {

    /** Empty cell symbol */
    public static final char EMPTY = '\0';
    
    /** Player mark symbol */
    public static final char PLAYER = 'X';
    
    /** Computer mark symbol */
    public static final char COMPUTER = 'O';
    
    /** Maximum board size */
    public static final int BOARD_SIZE = 9;

    /** Winning patterns for TicTacToe */
    private static final int[][] WIN_PATTERNS = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},  // horizontal
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},  // vertical
            {0, 4, 8}, {2, 4, 6}              // diagonal
    };

    /** Game board state */
    private final char[] mBoard = new char[BOARD_SIZE];
    
    /** List of indices that form the winning combination */
    private final List<Integer> mWinningIndices = new ArrayList<>();

    /**
     * Default constructor initializes the game board.
     */
    public TicTacToeGame() {
        reset();
    }

    /**
     * Resets the game board to its initial state.
     */
    public void reset() {
        Arrays.fill(mBoard, EMPTY);
        mWinningIndices.clear();
    }

    /**
     * Gets the symbol at a specific index.
     *
     * @param index The cell index.
     * @return The symbol at the index.
     */
    public char getCell(int index) {
        return mBoard[index];
    }

    /**
     * Sets the symbol at a specific index if it's empty.
     *
     * @param index The cell index.
     * @param mark  The mark to set.
     * @return true if successfully set, false if the cell was not empty.
     */
    public boolean setCell(int index, char mark) {
        if (mBoard[index] == EMPTY) {
            mBoard[index] = mark;
            return true;
        }
        return false;
    }

    /**
     * Checks if the board is completely filled.
     *
     * @return true if full, false otherwise.
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
     * Checks if a specific cell is empty.
     *
     * @param index The cell index.
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty(int index) {
        return mBoard[index] == EMPTY;
    }

    /**
     * Returns a copy of the current board state.
     *
     * @return The board array.
     */
    public char[] getBoard() {
        return mBoard.clone();
    }

    /**
     * Gets the indices of the winning combination.
     *
     * @return List of winning indices.
     */
    public List<Integer> getWinningIndices() {
        return new ArrayList<>(mWinningIndices);
    }

    /**
     * Checks if a player with the given mark has won.
     *
     * @param mark The mark to check.
     * @return true if won, false otherwise.
     */
    public boolean checkWin(char mark) {
        for (int[] pattern : WIN_PATTERNS) {
            if (mBoard[pattern[0]] == mark && mBoard[pattern[1]] == mark && mBoard[pattern[2]] == mark) {
                mWinningIndices.clear();
                mWinningIndices.add(pattern[0]);
                mWinningIndices.add(pattern[1]);
                mWinningIndices.add(pattern[2]);
                return true;
            }
        }
        return false;
    }

    /**
     * Finds a winning move for the given player if one exists.
     *
     * @param mark The mark to check.
     * @return The index of the winning move, or -1 if none found.
     */
    public int findWinningMove(char mark) {
        for (int[] pattern : WIN_PATTERNS) {
            int a = pattern[0];
            int b = pattern[1];
            int c = pattern[2];
            
            if (mBoard[a] == EMPTY && mBoard[b] == mark && mBoard[c] == mark) {
                return a;
            }
            if (mBoard[b] == EMPTY && mBoard[a] == mark && mBoard[c] == mark) {
                return b;
            }
            if (mBoard[c] == EMPTY && mBoard[a] == mark && mBoard[b] == mark) {
                return c;
            }
        }
        return -1;
    }
}
