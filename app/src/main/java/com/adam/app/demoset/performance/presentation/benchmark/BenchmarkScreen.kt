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

package com.adam.app.demoset.performance.presentation.benchmark

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.adam.app.demoset.R
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BenchmarkScreen(
    viewModel: BenchmarkViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.benchmark_title)) },
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
                text = stringResource(R.string.benchmark_desc),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.benchmark_micro_label), style = MaterialTheme.typography.labelLarge)
            Button(
                onClick = { viewModel.runAlgorithmComparison() },
                enabled = !uiState.isRunning,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.benchmark_run_algo_btn))
            }

            uiState.algorithmReport?.let { report ->
                BenchmarkReportCard(report)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.benchmark_macro_label), style = MaterialTheme.typography.labelLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = { viewModel.startUiBenchmark() },
                    enabled = !uiState.isRunning
                ) {
                    Text(stringResource(R.string.benchmark_start_ui_btn))
                }
                Button(
                    onClick = { viewModel.stopUiBenchmark() },
                    enabled = uiState.isRunning
                ) {
                    Text(stringResource(R.string.benchmark_stop_ui_btn))
                }
            }

            uiState.uiReport?.let { report ->
                BenchmarkReportCard(report)
            }
            
            if (uiState.isRunning) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun BenchmarkReportCard(report: com.adam.app.demoset.performance.domain.model.BenchmarkReport) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(R.string.benchmark_scenario_label, report.testScenarioName), style = MaterialTheme.typography.titleSmall)
            Text(stringResource(R.string.benchmark_type_label, report.type))
            Text(stringResource(R.string.benchmark_exec_time_label, report.executionTimeNs / 1_000_000.0))
            Text(stringResource(R.string.benchmark_median_frame_label, report.medianFrameTimeMs))
            Text(stringResource(R.string.benchmark_jank_count_label, report.jankCount))
            Text(stringResource(R.string.benchmark_jank_rate_label, report.jankPercentage))
        }
    }
}
