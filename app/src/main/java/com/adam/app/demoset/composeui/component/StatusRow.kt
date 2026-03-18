/**
 * Copyright (C) 2022 Adam Chen Demo set project. All rights reserved.
 * <p>
 * Description: This is a demo compose component.
 * </p>
 *
 * @author Adam Chen
 * @version 1.0 - 2026/03/18
 */
package com.adam.app.demoset.composeui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatusRow (
    label: String,
    value: Boolean
) {
    val color = if (value)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.error

    Row(
    modifier = Modifier
    .fillMaxWidth()
    .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = booleanText(value),
            color = color,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}