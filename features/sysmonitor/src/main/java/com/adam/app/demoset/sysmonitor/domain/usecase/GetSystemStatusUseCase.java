/*
 * MIT License
 *
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

package com.adam.app.demoset.sysmonitor.domain.usecase;

import androidx.lifecycle.LiveData;
import com.adam.app.demoset.sysmonitor.domain.model.SystemStatus;
import com.adam.app.demoset.sysmonitor.domain.repository.ISystemMonitorRepository;
import javax.inject.Inject;

/**
 * GetSystemStatusUseCase encapsulates the business logic for retrieving system status.
 * This follows the Use Case / Interactor pattern in Clean Architecture.
 */
public class GetSystemStatusUseCase {

    private final ISystemMonitorRepository repository;

    @Inject
    public GetSystemStatusUseCase(ISystemMonitorRepository repository) {
        this.repository = repository;
    }

    /**
     * Executes the use case to start monitoring and return the status stream.
     */
    public LiveData<SystemStatus> execute() {
        repository.startMonitoring();
        return repository.getSystemStatus();
    }

    /**
     * Cleans up resources when the use case is no longer needed.
     */
    public void cleanup() {
        repository.stopMonitoring();
    }
}
