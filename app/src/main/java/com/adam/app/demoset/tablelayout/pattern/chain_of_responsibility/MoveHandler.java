/**
 * This class is the abstract Move handler class.
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2025-11-12
 */
package com.adam.app.demoset.tablelayout.pattern.chain_of_responsibility;

import com.adam.app.demoset.tablelayout.model.TicTacToeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class MoveHandler {
    protected MoveHandler mNext;
    private static final Random RANDOM = new Random();

    public MoveHandler setNext(MoveHandler next) {
        mNext = next;
        return next;
    }

    public int handle(TicTacToeModel model) {
        int move = findMove(model);
        if (move != -1) {
            return move;
        }
        // next handler
        if (mNext != null) {
            return mNext.handle(model);
        }
        return -1;
    }

    protected abstract int findMove(TicTacToeModel model);

    /**
     * chooseRandomAvailableCell
     */
    protected int chooseRandomAvailableCell(TicTacToeModel model, int[] positions) {
        List<Integer> available = new ArrayList<>();
        for (int pos: positions) {
            if (model.isEmpty(pos)) {
                available.add(pos);
            }
        }
        if (available.isEmpty()) return -1;
        return available.get(RANDOM.nextInt(available.size()));
    }

}
