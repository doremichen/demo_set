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

package com.adam.app.demoset.tablelayout.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.tablelayout.domain.model.TicTacToeGame;
import com.adam.app.demoset.tablelayout.domain.repository.GameRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Implementation of GameRepository.
 */
@Singleton
public class GameRepositoryImpl implements GameRepository {

    /** Game engine entity */
    private final TicTacToeGame mGame = new TicTacToeGame();

    /** LiveData for the board state */
    private final MutableLiveData<char[]> mBoardLiveData = new MutableLiveData<>();

    /** LiveData for the game message */
    private final MutableLiveData<String> mMessageLiveData = new MutableLiveData<>();

    /** LiveData for the winning cell indices */
    private final MutableLiveData<List<Integer>> mWinningCellsLiveData = new MutableLiveData<>();

    /** Flag indicating if the game is over */
    private boolean mIsGameOver = false;

    /**
     * Constructor initializes the board LiveData.
     */
    @Inject
    public GameRepositoryImpl() {
        mBoardLiveData.setValue(mGame.getBoard());
    }

    @Override
    public LiveData<char[]> getBoard() {
        return mBoardLiveData;
    }

    @Override
    public LiveData<String> getMessage() {
        return mMessageLiveData;
    }

    @Override
    public LiveData<List<Integer>> getWinningCells() {
        return mWinningCellsLiveData;
    }

    @Override
    public void resetGame() {
        mGame.reset();
        mIsGameOver = false;
        mBoardLiveData.setValue(mGame.getBoard());
        mWinningCellsLiveData.setValue(null);
    }

    @Override
    public boolean makeMove(int index, char player) {
        if (mGame.setCell(index, player)) {
            mBoardLiveData.setValue(mGame.getBoard());
            return true;
        }
        return false;
    }

    @Override
    public boolean isGameOver() {
        return mIsGameOver;
    }

    @Override
    public void setGameOver(boolean gameOver) {
        mIsGameOver = gameOver;
    }

    @Override
    public void updateMessage(String message) {
        mMessageLiveData.setValue(message);
    }

    @Override
    public int findWinningMove(char player) {
        return mGame.findWinningMove(player);
    }

    @Override
    public boolean checkWin(char player) {
        boolean won = mGame.checkWin(player);
        if (won) {
            mWinningCellsLiveData.setValue(mGame.getWinningIndices());
        }
        return won;
    }

    @Override
    public boolean isBoardFull() {
        return mGame.isFull();
    }

    @Override
    public List<Integer> getWinningIndices() {
        return mGame.getWinningIndices();
    }

    @Override
    public boolean isEmpty(int index) {
        return mGame.isEmpty(index);
    }

    @Override
    public TicTacToeGame getGameEngine() {
        return mGame;
    }
}
