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

package com.adam.app.demoset.tablelayout.domain.repository;

import androidx.lifecycle.LiveData;

import com.adam.app.demoset.tablelayout.domain.model.TicTacToeGame;

import java.util.List;

/**
 * Repository interface for managing TicTacToe game state.
 */
public interface GameRepository {

    /**
     * Gets the current board state.
     *
     * @return LiveData containing the board array.
     */
    LiveData<char[]> getBoard();

    /**
     * Gets the current game message.
     *
     * @return LiveData containing the message string.
     */
    LiveData<String> getMessage();

    /**
     * Gets the winning cell indices.
     *
     * @return LiveData containing the list of winning indices.
     */
    LiveData<List<Integer>> getWinningCells();

    /**
     * Resets the game to its initial state.
     */
    void resetGame();

    /**
     * Makes a move on the board.
     *
     * @param index  The cell index.
     * @param player The player mark.
     * @return true if the move was valid and executed.
     */
    boolean makeMove(int index, char player);

    /**
     * Checks if the game is over.
     *
     * @return true if game over, false otherwise.
     */
    boolean isGameOver();

    /**
     * Sets the game over status.
     *
     * @param gameOver The game over status.
     */
    void setGameOver(boolean gameOver);

    /**
     * Updates the game message.
     *
     * @param message The message to set.
     */
    void updateMessage(String message);
    
    /**
     * Finds a winning move for the given player.
     * 
     * @param player The player mark.
     * @return The cell index of the winning move, or -1.
     */
    int findWinningMove(char player);
    
    /**
     * Checks if a player has won.
     * 
     * @param player The player mark.
     * @return true if won.
     */
    boolean checkWin(char player);
    
    /**
     * Checks if the board is full.
     * 
     * @return true if full.
     */
    boolean isBoardFull();

    /**
     * Gets the winning indices from the current game state.
     * 
     * @return List of indices.
     */
    List<Integer> getWinningIndices();

    /**
     * Checks if a cell is empty.
     * 
     * @param index The cell index.
     * @return true if empty.
     */
    boolean isEmpty(int index);

    /**
     * Gets the game engine instance.
     *
     * @return TicTacToeGame instance.
     */
    TicTacToeGame getGameEngine();
}
