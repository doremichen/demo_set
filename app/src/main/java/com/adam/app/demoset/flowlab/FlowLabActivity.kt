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

package com.adam.app.demoset.flowlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.adam.app.demoset.R
import com.adam.app.demoset.flowlab.viewmodel.FlowLabViewModel
import com.adam.app.demoset.utils.Utils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Dimensions for FlowLab UI.
 */
object FlowLabDimens {
    val StandardPadding = 16.dp
    val TinyPadding = 8.dp
    val ExtraTinyPadding = 2.dp
    val CardElevation = 4.dp
    val CornerRadius = 12.dp
    val ConsoleCornerRadius = 8.dp
    val ConsoleFontSize = 12.sp
}

/**
 * Activity for the Modern Flow Lab demo.
 */
class FlowLabActivity : ComponentActivity() {

    private val viewModel: FlowLabViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Observe SharedFlow events (Toast) in a lifecycle-aware manner.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.toastEvent.collectLatest { timestamp ->
                    val message = getString(R.string.demo_flow_toast_msg, timestamp.toString())
                    Utils.showToast(this@FlowLabActivity, message)
                    viewModel.addLog(getString(R.string.demo_flow_log_toast_received, message))
                }
            }
        }

        setContent {
            FlowLabScreen(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowLabScreen(viewModel: FlowLabViewModel) {
    val counter by viewModel.counterState.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Auto-scroll to the bottom when new logs are added.
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.demo_flow_lab_title)) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(FlowLabDimens.StandardPadding)
        ) {
            // Technical Instruction area
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(FlowLabDimens.CornerRadius),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.demo_flow_instruction),
                    modifier = Modifier.padding(FlowLabDimens.StandardPadding),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(FlowLabDimens.StandardPadding))

            // State Control Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(FlowLabDimens.CardElevation)
            ) {
                Column(modifier = Modifier.padding(FlowLabDimens.StandardPadding)) {
                    Text(
                        text = stringResource(R.string.demo_flow_counter_format, counter),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(FlowLabDimens.StandardPadding))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(FlowLabDimens.TinyPadding)
                    ) {
                        Button(
                            onClick = { 
                                viewModel.incrementCounter() 
                                viewModel.addLog(context.getString(R.string.demo_flow_log_increment))
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.demo_flow_increment_btn))
                        }
                        OutlinedButton(
                            onClick = { 
                                viewModel.triggerEvent() 
                                viewModel.addLog(context.getString(R.string.demo_flow_log_event))
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.demo_flow_trigger_event_btn))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(FlowLabDimens.StandardPadding))

            // Console-style Event Log
            Text(
                text = stringResource(R.string.demo_flow_event_logs), 
                style = MaterialTheme.typography.titleSmall
            )
            
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = FlowLabDimens.TinyPadding)
                    .background(Color(0xFF263238), RoundedCornerShape(FlowLabDimens.ConsoleCornerRadius))
                    .padding(FlowLabDimens.TinyPadding)
            ) {
                items(logs) { log ->
                    Text(
                        text = log,
                        color = Color.Green,
                        fontFamily = FontFamily.Monospace,
                        fontSize = FlowLabDimens.ConsoleFontSize,
                        modifier = Modifier.padding(vertical = FlowLabDimens.ExtraTinyPadding)
                    )
                }
            }

            Button(
                onClick = { 
                    viewModel.clearLog() 
                    viewModel.addLog(context.getString(R.string.demo_flow_log_clear))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.demo_material_clear_log))
            }
        }
    }
}
