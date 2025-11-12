/**
 * This class is the tictactoe view model.
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2025-11-11
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
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.tablelayout.model.TicTacToeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

        int index = empty.get(new Random().nextInt(empty.size()));
        handleMove(index, TicTacToeModel.COMPUTER, mContext.getString(R.string.demo_tablelayout_computer_win_msg));
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
