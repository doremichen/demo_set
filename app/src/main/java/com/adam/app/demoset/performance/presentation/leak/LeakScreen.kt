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

package com.adam.app.demoset.performance.presentation.leak

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.adam.app.demoset.R
import com.adam.app.demoset.performance.domain.model.LeakConfig

// used to simulate a memory leak with dynamic size
class LeakDummyTarget {
    // Randomly generate 50KB ~ 500KB data to simulate real object size
    private val payload = ByteArray(
        (LeakConfig.PAYLOAD_MIN_KB..LeakConfig.PAYLOAD_MAX_KB).random() * LeakConfig.BYTES_PER_KB
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeakScreen(
    viewModel: LeakViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // monitor navigation event and navigate back
    LaunchedEffect(key1 = Unit) {
        viewModel.navigateToMainEvent.collect {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.leak_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.leak_canary_scenario),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // simulate a memory leak
                    val target = LeakDummyTarget()
                    viewModel.triggerLeak(target)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.leak_simulate_btn))
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    // Clear all static strong references before execution to ensure this session is clean
                    viewModel.clearLeakedReferences()
                    val target = LeakDummyTarget()
                    viewModel.watchInstance(target, "Manual watch test")
                    Toast.makeText(context, R.string.leak_watch_toast, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.leak_watch_btn))
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { viewModel.clearReports() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.leak_clear_history_btn))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.leak_current_status, uiState.status),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(stringResource(R.string.leak_report_list), style = MaterialTheme.typography.titleSmall)

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(uiState.reports) { report ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF3E0)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(stringResource(R.string.leak_class_label, report.targetClassName), style = MaterialTheme.typography.bodyMedium)
                            Text(stringResource(R.string.leak_path_label, report.leakTraceShort), style = MaterialTheme.typography.bodySmall)
                            Text(stringResource(R.string.leak_size_label, report.retainedHeapByteSize), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            if (uiState.reports.isEmpty()) {
                Text(stringResource(R.string.leak_no_report),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray)
            }
        }
    }

    if (uiState.isAnalyzing) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = { },
            title = { Text(stringResource(R.string.leak_analyzing_title)) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.leak_analyzing))
                }
            }
        )
    }
}
