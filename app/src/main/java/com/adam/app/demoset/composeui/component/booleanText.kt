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

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.adam.app.demoset.R

@Composable
fun booleanText(value: Boolean): String {
    return if (value) {
        stringResource(R.string.demo_compose_status_true)
    } else {
        stringResource(R.string.demo_compose_status_false)
    }
}