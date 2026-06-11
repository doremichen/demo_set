package com.adam.app.demoset.performance.framework.LeakCanary

import com.adam.app.demoset.performance.domain.model.LeakReport
import com.adam.app.demoset.performance.domain.model.LeakStatus
import com.adam.app.demoset.performance.domain.repository.LeakRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import leakcanary.AppWatcher
import leakcanary.LeakCanary

class LeakCanaryBridge : LeakRepository {

    private val _leakReports = MutableSharedFlow<LeakReport>(replay = 1)

    // used to make memory leak
    companion object {
        private val leakedLeash = mutableListOf<Any>()

        fun forceLeak(obj: Any) {
            leakedLeash.add(obj)
        }

        fun clearLeakedReferences() {
            leakedLeash.clear()
        }
    }

    override fun watchInstance(instance: Any, description: String) {
        AppWatcher.objectWatcher.watch(instance, description)
    }

    override fun simulateLeak(instance: Any) {
        forceLeak(instance)
    }

    override fun clearLeakedReferences() {
        Companion.clearLeakedReferences()
    }

    override fun getLeakReports(): Flow<LeakReport> {
        // In a real app, you would register a LeakCanary Listener and emit reports here.
        // For this demo, we'll return the flow.
        return _leakReports
    }
}
