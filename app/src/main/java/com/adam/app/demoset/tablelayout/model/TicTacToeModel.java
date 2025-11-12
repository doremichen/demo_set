/**
 * This class is the tictac model class.
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2025-11-11
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
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
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

}
