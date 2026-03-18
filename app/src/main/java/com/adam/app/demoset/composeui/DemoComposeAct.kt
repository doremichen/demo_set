/**
 * Copyright (C) 2022 Adam Chen Demo set project. All rights reserved.
 * <p>
 * Description: This is a demo compose ui activity.
 * </p>
 *
 * @author Adam Chen
 * @version 1.0 - 2026/03/18
 */
package com.adam.app.demoset.composeui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adam.app.demoset.R
import com.adam.app.demoset.composeui.component.StatusRow
import com.adam.app.demoset.composeui.viewmodel.DemoComposeViewModel


class DemoComposeAct : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set content view
        setContent(
            content = {
                DemoComposeScreen()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoComposeScreen(vm: DemoComposeViewModel = viewModel()) {

    val state by vm.uiState.collectAsState()
    val listState = rememberLazyListState()

    // auto scroll to top when the newest log is added
    LaunchedEffect(state.logs.size) {
        if (state.logs.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.demo_compose_title))
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // --- 新增：Demo 說明區塊 ---
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Jetpack Compose 技術實踐",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "本介面採用聲明式 UI 構建，透過 StateFlow 驅動 Recomposition。重點展示：\n" +
                                "• 狀態單向流 (UDF) 控制服務生命週期\n" +
                                "• LazyColumn 配合穩定 Key 值優化長列表效能\n" +
                                "• 響應式佈局 (Row/Column) 實現跨設備適配",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )
                }
            }


            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        stringResource(R.string.demo_compose_service_status_label),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(12.dp))

            // Log
            Text(
                "Logs",
                style = MaterialTheme.typography.titleSmall
            )
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // 關鍵
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    count = state.logs.size,
                    key = { index -> state.logs[index].id }
                ) { entry ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = stringResource(state.logs[entry].resId),
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Text("Quick Controls", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp) // 按鈕間距縮小以適應橫向排列
            ) {
                ControlButton(R.string.demo_compose_btn_start, MaterialTheme.colorScheme.primary) { vm.startService() }
                ControlButton(R.string.demo_compose_btn_stop, MaterialTheme.colorScheme.error) { vm.stopService() }
                ControlButton(R.string.demo_compose_btn_bind, MaterialTheme.colorScheme.secondary) { vm.bindService() }
                ControlButton(R.string.demo_compose_btn_unbind, MaterialTheme.colorScheme.outline) { vm.unbindService() }
            }

        }
    }
}

@Composable
fun RowScope.ControlButton(resId: Int, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f), // 四個按鈕各佔 25%
        contentPadding = PaddingValues(horizontal = 4.dp), // 減少內邊距防止文字被裁切
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(stringResource(resId), style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}