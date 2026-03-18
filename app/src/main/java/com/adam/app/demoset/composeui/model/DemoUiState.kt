/**
 * Copyright (C) 2022 Adam Chen Demo set project. All rights reserved.
 * <p>
 * Description: This is a Demo ui state value object
 * </p>
 *
 * @author Adam Chen
 * @version 1.0 - 2026/03/18
 */
package com.adam.app.demoset.composeui.model

data class LogEntry(
    val id: Long = System.nanoTime(), // 使用奈秒作為唯一標識
    val resId: Int
)

data class DemoUiState(
    val isServiceRunning: Boolean = false,
    val isBound: Boolean = false,
    val logs: List<LogEntry> = emptyList()
)

