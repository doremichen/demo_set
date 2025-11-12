/**
 * This class is used to bind the data to the view.
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2025-11-11
 */
package com.adam.app.demoset.tablelayout;

import android.graphics.Color;
import android.widget.Button;

import androidx.databinding.BindingAdapter;

import com.adam.app.demoset.Utils;

import java.util.List;

public class BindingAdapters {
    // TAG
    private static final String TAG = "BindingAdapters";


    @BindingAdapter(value = {"board", "cellIndex", "winningCells"}, requireAll = true)
    public static void bindBoardCell(Button button, char[] board, int index, List<Integer> winningCells) {
        Utils.log(TAG, "bindBoardCell: " + index);
        // input check
        if (board == null ||index < 0 || index >= board.length) {
            // clear button info
            button.setText("");
            button.setBackgroundColor(Color.LTGRAY);
            return;
        }

        // Display
        char c = board[index];
        button.setText((c == '\0')? "": String.valueOf(c));

        // color difference
        int baseColor = Color.LTGRAY;
        int textColor = Color.BLACK;

        if (c == 'X') {
            textColor = Color.BLUE;
        } else if (c == 'O') {
            textColor = Color.RED;
        }

        if (winningCells != null && winningCells.contains(index)) {
            baseColor = Color.YELLOW;
        }

        button.setBackgroundColor(baseColor);
        button.setTextColor(textColor);

    }

}
