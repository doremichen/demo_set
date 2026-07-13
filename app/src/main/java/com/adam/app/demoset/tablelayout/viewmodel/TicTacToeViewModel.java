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

package com.adam.app.demoset.tablelayout.viewmodel;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.adam.app.demoset.R;
import com.adam.app.demoset.tablelayout.domain.model.TicTacToeGame;
import com.adam.app.demoset.tablelayout.domain.usecase.TicTacToeUseCase;
import com.adam.app.demoset.utils.Utils;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * ViewModel for TicTacToe game.
 */
@HiltViewModel
public class TicTacToeViewModel extends ViewModel {

    /** Tag for logging */
    private static final String TAG = "TicTacToeViewModel";
    
    /** Delay for auto-reset in milliseconds */
    private static final long RESET_DELAY_MS = 3000L;

    /** Application context */
    private final Context mContext;
    
    /** TicTacToe use case entry point */
    private final TicTacToeUseCase mUseCase;

    /**
     * Constructor with dependency injection.
     */
    @Inject
    public TicTacToeViewModel(@ApplicationContext Context context,
                              TicTacToeUseCase useCase) {
        mContext = context;
        mUseCase = useCase;
        
        // Initial setup
        mUseCase.startNewGame(mContext.getString(R.string.demo_tablelayout_status_message));
    }

    public LiveData<char[]> getBoardLiveData() {
        return mUseCase.getBoard();
    }

    public LiveData<String> getMessageLiveData() {
        return mUseCase.getMessage();
    }

    public LiveData<List<Integer>> getWinningCellsLiveData() {
        return mUseCase.getWinningCells();
    }

    /**
     * Starts a new game.
     */
    public void startNewGame() {
        mUseCase.startNewGame(mContext.getString(R.string.demo_tablelayout_game_start_msg));
    }

    /**
     * Handles player move.
     *
     * @param index Cell index.
     */
    public void playerMove(int index) {
        Utils.log(TAG, "playerMove: " + index);
        boolean moved = mUseCase.makeMove(index, TicTacToeGame.PLAYER,
                mContext.getString(R.string.demo_tablelayout_player_win_msg),
                mContext.getString(R.string.demo_tablelayout_tie_msg));
        
        if (!moved) {
            Utils.showToast(mContext, "Game over or this cell is not empty");
            return;
        }
        
        if (mUseCase.isGameOver()) {
            resetLater();
            return;
        }
        
        computerMove();
    }

    /**
     * Handles computer move.
     */
    private void computerMove() {
        mUseCase.computerMove(mContext.getString(R.string.demo_tablelayout_computer_win_msg),
                mContext.getString(R.string.demo_tablelayout_tie_msg));
        
        if (mUseCase.isGameOver()) {
            resetLater();
        }
    }

    /**
     * Resets game after a delay.
     */
    private void resetLater() {
        new Handler(Looper.getMainLooper()).postDelayed(this::startNewGame, RESET_DELAY_MS);
    }
}
