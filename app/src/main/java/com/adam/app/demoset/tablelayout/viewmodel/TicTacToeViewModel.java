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

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.tablelayout.model.TicTacToeModel;
import com.adam.app.demoset.tablelayout.pattern.builder.MoveChainBuilder;
import com.adam.app.demoset.tablelayout.pattern.chain_of_responsibility.MoveHandler;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeViewModel extends AndroidViewModel {

    // TAG
    private static final String TAG = "TicTacToeViewModel";

    private final Context mContext;

    // model
    private final TicTacToeModel mModel;
    // Live data
    private final MutableLiveData<char[]> mBoardLiveData = new MutableLiveData<>(null);
    private final MutableLiveData<String> mMessageLiveData = new MutableLiveData<>("");
    private final MutableLiveData<List<Integer>> mWinningCellsLiveData = new MutableLiveData<>(null);
    private boolean mIsGameOver = false;

    public TicTacToeViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mModel = new TicTacToeModel();
        // initial status message
        String msg = mContext.getString(R.string.demo_tablelayout_status_message);
        mMessageLiveData.setValue(msg);
    }

    public LiveData<char[]> getBoardLiveData() {
        return mBoardLiveData;
    }

    public LiveData<String> getMessageLiveData() {
        return mMessageLiveData;
    }

    public LiveData<List<Integer>> getWinningCellsLiveData() {
        return mWinningCellsLiveData;
    }


    /**
     * start new game
     */
    public void startNewGame() {
        mModel.reset();
        mIsGameOver = false;
        mBoardLiveData.setValue(mModel.getBoard());
        mMessageLiveData.setValue(mContext.getString(R.string.demo_tablelayout_game_start_msg));
    }

    /**
     * player move
     *
     * @param index cell index
     */
    public void playerMove(int index) {
        Utils.log(TAG, "playerMove: " + index);
        // check game over or not empty in cell by index
        if (mIsGameOver || !mModel.isEmpty(index)) {
            // SHOW TOAST
            Utils.showToast(mContext, "Game over or this cell is not empty");
            return;
        }

        boolean end = handleMove(index, TicTacToeModel.PLAYER, mContext.getString(R.string.demo_tablelayout_player_win_msg));
        if (end) return;

        // computer move
        computerMove();
    }

    /**
     * computer move
     */
    private void computerMove() {
        Utils.log(TAG, "computerMove: ");
        List<Integer> empty = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (mModel.isEmpty(i)) empty.add(i);
        }
        if (empty.isEmpty()) return;
        // step1: find player winner cell possibility
        int blockIndex = mModel.findWinningMove(TicTacToeModel.PLAYER);
        if (blockIndex != -1) {
            handleMove(blockIndex, TicTacToeModel.COMPUTER, mContext.getString(R.string.demo_tablelayout_computer_win_msg));
            return;
        }
        // step2: find computer winner cell possibility
        int winIndex = mModel.findWinningMove(TicTacToeModel.COMPUTER);
        if (winIndex != -1) {
            handleMove(winIndex, TicTacToeModel.COMPUTER, mContext.getString(R.string.demo_tablelayout_computer_win_msg));
            return;
        }

        // step3: prefer center -> corner -> edges
        preferMove();

    }

    private void preferMove() {

        // move handle
        MoveHandler chain = MoveChainBuilder
                .forDifficulty(MoveChainBuilder.Difficulty.HARD)
                .build();

        int moveIndex = chain.handle(mModel);
        if (moveIndex != -1) {
            handleMove(moveIndex, TicTacToeModel.COMPUTER, mContext.getString(R.string.demo_tablelayout_computer_win_msg));
        }

    }

    /**
     * handle player move
     *
     * @param index  cell index
     * @param player player char
     * @param s      message
     * @return true if game over else false
     */
    private boolean handleMove(int index, char player, String s) {
        Utils.log(TAG, "handleMove: " + index);
        // put in cell
        boolean end = mModel.setCell(index, player);
        if (!end) {
            // show toast
            Utils.showToast(mContext, "This cell is not empty");
            return false;
        }
        Utils.log(TAG, "update board!!!");

        // update bord
        mBoardLiveData.setValue(mModel.getBoard());

        // check winner
        if (mModel.checkWin(player)) {
            mIsGameOver = true;
            mMessageLiveData.setValue(s);
            mWinningCellsLiveData.setValue(mModel.getCellWins());
            resetLater();
            return true;
        }

        // check tie
        if (mModel.isFull()) {
            mIsGameOver = true;
            mMessageLiveData.setValue(mContext.getString(R.string.demo_tablelayout_tie_msg));
            resetLater();
            return true;
        }

        return false;
    }

    /**
     * reset game after 2 seconds
     */
    private void resetLater() {
        new Handler(Looper.getMainLooper()).postDelayed(this::startNewGame, 3000L);
    }


}
