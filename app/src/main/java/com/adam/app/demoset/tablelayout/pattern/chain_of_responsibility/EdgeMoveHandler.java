/**
 * This class is the edge move handler.
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2025-11-12
 */
package com.adam.app.demoset.tablelayout.pattern.chain_of_responsibility;

import com.adam.app.demoset.tablelayout.model.TicTacToeModel;

public class EdgeMoveHandler extends MoveHandler {

    private static final int[] EDGE_MOVES = {0, 2, 6, 8};

    @Override
    protected int findMove(TicTacToeModel model) {
        return chooseRandomAvailableCell(model, EDGE_MOVES);
    }
}
