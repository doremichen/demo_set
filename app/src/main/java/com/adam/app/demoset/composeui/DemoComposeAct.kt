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

package com.adam.app.demoset.composeui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adam.app.demoset.R
import com.adam.app.demoset.composeui.component.StatusRow
import com.adam.app.demoset.composeui.model.DemoUiState
import com.adam.app.demoset.composeui.viewmodel.DemoComposeViewModel

/**
 * Dimensions and constants for DemoCompose UI to avoid magic numbers.
 */
object DemoComposeDimens {
    val StandardPadding = 16.dp
    val SmallPadding = 12.dp
    val TinyPadding = 8.dp
    val ExtraTinyPadding = 4.dp
    val WideScreenBreakpoint = 600.dp
    val CardCornerRadius = 16.dp
    val SectionCornerRadius = 12.dp
    val ButtonCornerRadius = 8.dp
    val CardElevation = 2.dp
}

@Composable
fun DemoAppTheme(content: @Composable () -> Unit) {
    // Support Material 3 Dynamic Color on Android 12+ (API 31+)
    val context = LocalContext.current
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isSystemInDarkTheme()) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        else -> MaterialTheme.colorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

class DemoComposeAct : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemoAppTheme {
                DemoComposeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoComposeScreen(vm: DemoComposeViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val listState = rememberLazyListState()

    // Auto-scroll to top when a new log entry is added
    LaunchedEffect(state.logs.size) {
        if (state.logs.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.demo_compose_title)) })
        }
    ) { padding ->
        // Responsive Layout: Use BoxWithConstraints to detect screen width
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val isWideScreen = this.maxWidth > DemoComposeDimens.WideScreenBreakpoint

            if (isWideScreen) {
                // Tablet/Landscape layout: Split screen into two columns
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(DemoComposeDimens.StandardPadding),
                    horizontalArrangement = Arrangement.spacedBy(DemoComposeDimens.StandardPadding)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        InstructionSection()
                        Spacer(modifier = Modifier.height(DemoComposeDimens.StandardPadding))
                        StatusSection(state)
                        Spacer(modifier = Modifier.height(DemoComposeDimens.StandardPadding))
                        ControlSection(vm)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        LogSection(state, listState)
                    }
                }
            } else {
                // Phone/Portrait layout: Vertical stack
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(DemoComposeDimens.StandardPadding)
                ) {
                    InstructionSection()
                    Spacer(modifier = Modifier.height(DemoComposeDimens.StandardPadding))
                    StatusSection(state)
                    Spacer(modifier = Modifier.height(DemoComposeDimens.SmallPadding))
                    LogSection(state, listState, Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(DemoComposeDimens.SmallPadding))
                    ControlSection(vm)
                }
            }
        }
    }
}

@Composable
fun InstructionSection() {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(DemoComposeDimens.SectionCornerRadius)
    ) {
        Column(modifier = Modifier.padding(DemoComposeDimens.SmallPadding)) {
            Text(
                text = stringResource(R.string.demo_compose_instruction_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(DemoComposeDimens.ExtraTinyPadding))
            Text(
                text = stringResource(R.string.demo_compose_instruction_content),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun StatusSection(state: DemoUiState) {
    Card(
        shape = RoundedCornerShape(DemoComposeDimens.CardCornerRadius),
        elevation = CardDefaults.cardElevation(DemoComposeDimens.CardElevation),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(DemoComposeDimens.StandardPadding)) {
            Text(
                stringResource(R.string.demo_compose_service_status_label),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(DemoComposeDimens.TinyPadding))
            StatusRow(
                stringResource(R.string.demo_compose_service_running),
                state.isServiceRunning
            )
            StatusRow(
                stringResource(R.string.demo_compose_service_bound),
                state.isBound
            )
        }
    }
}

@Composable
fun LogSection(
    state: DemoUiState,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(stringResource(R.string.demo_compose_logs_label), style = MaterialTheme.typography.titleSmall)
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DemoComposeDimens.TinyPadding),
            contentPadding = PaddingValues(vertical = DemoComposeDimens.TinyPadding)
        ) {
            items(
                count = state.logs.size,
                key = { index -> state.logs[index].id }
            ) { entry ->
                Card(
                    shape = RoundedCornerShape(DemoComposeDimens.SectionCornerRadius),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = stringResource(state.logs[entry].resId),
                        modifier = Modifier.padding(DemoComposeDimens.SmallPadding),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun ControlSection(vm: DemoComposeViewModel) {
    Column {
        Text(stringResource(R.string.demo_compose_quick_controls_label), style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(DemoComposeDimens.TinyPadding))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DemoComposeDimens.ExtraTinyPadding)
        ) {
            ControlButton(R.string.demo_compose_btn_start, MaterialTheme.colorScheme.primary) { vm.startService() }
            ControlButton(R.string.demo_compose_btn_stop, MaterialTheme.colorScheme.error) { vm.stopService() }
            ControlButton(R.string.demo_compose_btn_bind, MaterialTheme.colorScheme.secondary) { vm.bindService() }
            ControlButton(R.string.demo_compose_btn_unbind, MaterialTheme.colorScheme.outline) { vm.unbindService() }
        }
    }
}

@Composable
fun RowScope.ControlButton(resId: Int, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(horizontal = DemoComposeDimens.ExtraTinyPadding),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(DemoComposeDimens.ButtonCornerRadius)
    ) {
        Text(stringResource(resId), style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}
