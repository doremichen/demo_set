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
