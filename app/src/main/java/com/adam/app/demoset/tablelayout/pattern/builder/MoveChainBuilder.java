/**
 * This class is used to build move chain for the game.
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2025-11-12
 */
package com.adam.app.demoset.tablelayout.pattern.builder;

import com.adam.app.demoset.tablelayout.pattern.chain_of_responsibility.CenterMoveHandler;
import com.adam.app.demoset.tablelayout.pattern.chain_of_responsibility.CornerMoveHandler;
import com.adam.app.demoset.tablelayout.pattern.chain_of_responsibility.EdgeMoveHandler;
import com.adam.app.demoset.tablelayout.pattern.chain_of_responsibility.MoveHandler;

import java.util.ArrayList;
import java.util.List;

public class MoveChainBuilder {

    // list of handler
    private List<MoveHandler> handlers = new ArrayList<>();

    public static MoveChainBuilder forDifficulty(Difficulty difficulty) {
        MoveChainBuilder builder = new MoveChainBuilder();
        switch (difficulty) {
            case EASY:
                builder.addHandler(new CenterMoveHandler());
                break;
            case MEDIUM:
                builder.addHandler(new CornerMoveHandler());
                builder.addHandler(new EdgeMoveHandler());
                break;
            case HARD:
                builder.addHandler(new CenterMoveHandler());
                builder.addHandler(new CornerMoveHandler());
                builder.addHandler(new EdgeMoveHandler());
                break;
        }
        return builder;
    }

    public MoveChainBuilder addHandler(MoveHandler handler) {
        handlers.add(handler);
        return this;
    }

    /**
     * build the chain
     *
     * @return the first handler
     */
    public MoveHandler build() {
        if (handlers.isEmpty()) {
            return null;
        }

        for (int i = 0; i < handlers.size() - 1; i++) {
            handlers.get(i).setNext(handlers.get(i + 1));
        }
        return handlers.get(0);
    }

    /**
     * difficulty of the game
     */
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}
