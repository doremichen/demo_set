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
