/**
 * This class is the coner move handler.
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2025-11-12
 */
package com.adam.app.demoset.tablelayout.pattern.chain_of_responsibility;

import com.adam.app.demoset.tablelayout.model.TicTacToeModel;

public class CornerMoveHandler extends MoveHandler {
    private static final int[] CORNERS = {0, 2, 6, 8};

    @Override
    protected int findMove(TicTacToeModel model) {
        return chooseRandomAvailableCell(model, CORNERS);
    }
}
