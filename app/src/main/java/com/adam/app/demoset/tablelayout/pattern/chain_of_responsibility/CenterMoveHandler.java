/**
 * This class is the center move handler.
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2025-11-12
 */
package com.adam.app.demoset.tablelayout.pattern.chain_of_responsibility;

import com.adam.app.demoset.tablelayout.model.TicTacToeModel;

public class CenterMoveHandler extends MoveHandler {

    private static final int CENTER = 4;

    @Override
    protected int findMove(TicTacToeModel model) {
        return (model.isEmpty(CENTER))? CENTER : -1;
    }
}
