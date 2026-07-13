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

import com.adam.app.demoset.tablelayout.domain.model.TicTacToeGame;
import com.adam.app.demoset.tablelayout.domain.repository.GameRepository;
import com.adam.app.demoset.tablelayout.pattern.builder.MoveChainBuilder;
import com.adam.app.demoset.tablelayout.pattern.chainofresponsibility.MoveHandler;

/**
 * Strategy enum for TicTacToe use cases.
 */
public enum TicTacToeUseCases {

    /** Start a new game */
    START_NEW_GAME {
        @Override
        public <T> T execute(GameRepository repository, Object... args) {
            String startMessage = (String) args[0];
            repository.resetGame();
            repository.updateMessage(startMessage);
            return null;
        }
    },

    /** Make a player or computer move */
    MAKE_MOVE {
        @Override
        public <T> T execute(GameRepository repository, Object... args) {
            int index = (int) args[0];
            char player = (char) args[1];
            String winMessage = (String) args[2];
            String tieMessage = (String) args[3];

            if (repository.isGameOver() || !repository.isEmpty(index)) {
                return (T) Boolean.FALSE;
            }

            boolean moved = repository.makeMove(index, player);
            if (moved) {
                if (repository.checkWin(player)) {
                    repository.setGameOver(true);
                    repository.updateMessage(winMessage);
                } else if (repository.isBoardFull()) {
                    repository.setGameOver(true);
                    repository.updateMessage(tieMessage);
                }
            }
            return (T) Boolean.valueOf(moved);
        }
    },

    /** Execute computer AI move logic */
    COMPUTER_MOVE {
        @Override
        public <T> T execute(GameRepository repository, Object... args) {
            String winMessage = (String) args[0];
            String tieMessage = (String) args[1];

            if (repository.isGameOver()) {
                return null;
            }

            // Step 1: Block player if they are about to win
            int blockIndex = repository.findWinningMove(TicTacToeGame.PLAYER);
            if (blockIndex != -1) {
                MAKE_MOVE.execute(repository, blockIndex, TicTacToeGame.COMPUTER, winMessage, tieMessage);
                return null;
            }

            // Step 2: Try to win
            int winIndex = repository.findWinningMove(TicTacToeGame.COMPUTER);
            if (winIndex != -1) {
                MAKE_MOVE.execute(repository, winIndex, TicTacToeGame.COMPUTER, winMessage, tieMessage);
                return null;
            }

            // Step 3: Strategy chain (Center -> Corner -> Edge)
            MoveHandler chain = MoveChainBuilder.forDifficulty(MoveChainBuilder.Difficulty.HARD).build();
            if (chain != null) {
                int moveIndex = chain.handle(repository.getGameEngine());
                if (moveIndex != -1) {
                    MAKE_MOVE.execute(repository, moveIndex, TicTacToeGame.COMPUTER, winMessage, tieMessage);
                }
            }
            return null;
        }
    },

    /** Get board state LiveData */
    GET_BOARD {
        @Override
        public <T> T execute(GameRepository repository, Object... args) {
            return (T) repository.getBoard();
        }
    },

    /** Get game message LiveData */
    GET_MESSAGE {
        @Override
        public <T> T execute(GameRepository repository, Object... args) {
            return (T) repository.getMessage();
        }
    },

    /** Get winning cells LiveData */
    GET_WINNING_CELLS {
        @Override
        public <T> T execute(GameRepository repository, Object... args) {
            return (T) repository.getWinningCells();
        }
    },

    /** Check if game is over */
    IS_GAME_OVER {
        @Override
        public <T> T execute(GameRepository repository, Object... args) {
            return (T) Boolean.valueOf(repository.isGameOver());
        }
    };

    /**
     * Executes the strategy.
     *
     * @param repository The game repository.
     * @param args       Arguments for the use case.
     * @param <T>        Return type.
     * @return The result of execution.
     */
    public abstract <T> T execute(GameRepository repository, Object... args);
}
