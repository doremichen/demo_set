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

package com.adam.app.demoset.tablelayout.domain.usecase;

import androidx.lifecycle.LiveData;

import com.adam.app.demoset.tablelayout.domain.repository.GameRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Entry point for TicTacToe use cases.
 */
public class TicTacToeUseCase {

    /** Game repository */
    private final GameRepository mRepository;

    @Inject
    public TicTacToeUseCase(GameRepository repository) {
        mRepository = repository;
    }

    /**
     * Starts a new game.
     *
     * @param msg The start message.
     */
    public void startNewGame(String msg) {
        TicTacToeUseCases.START_NEW_GAME.execute(mRepository, msg);
    }

    /**
     * Makes a move.
     *
     * @param index      Cell index.
     * @param player     Player mark.
     * @param winMessage Win message.
     * @param tieMessage Tie message.
     * @return true if moved.
     */
    public boolean makeMove(int index, char player, String winMessage, String tieMessage) {
        return TicTacToeUseCases.MAKE_MOVE.execute(mRepository, index, player, winMessage, tieMessage);
    }

    /**
     * Executes computer move.
     *
     * @param winMessage Win message.
     * @param tieMessage Tie message.
     */
    public void computerMove(String winMessage, String tieMessage) {
        TicTacToeUseCases.COMPUTER_MOVE.execute(mRepository, winMessage, tieMessage);
    }

    /**
     * Gets the board LiveData.
     *
     * @return Board LiveData.
     */
    public LiveData<char[]> getBoard() {
        return TicTacToeUseCases.GET_BOARD.execute(mRepository);
    }

    /**
     * Gets the message LiveData.
     *
     * @return Message LiveData.
     */
    public LiveData<String> getMessage() {
        return TicTacToeUseCases.GET_MESSAGE.execute(mRepository);
    }

    /**
     * Gets the winning cells LiveData.
     *
     * @return Winning cells LiveData.
     */
    public LiveData<List<Integer>> getWinningCells() {
        return TicTacToeUseCases.GET_WINNING_CELLS.execute(mRepository);
    }

    /**
     * Checks if game is over.
     *
     * @return true if game over.
     */
    public boolean isGameOver() {
        return TicTacToeUseCases.IS_GAME_OVER.execute(mRepository);
    }
}
