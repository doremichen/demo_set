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

package com.adam.app.demoset.quicksetting.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel for the Quick Setting demo main page.
 * Manages navigation events and UI state.
 */
public class QuickSettingViewModel extends ViewModel {

    /**
     * Enum defining supported navigation events for this feature.
     */
    public enum NavigationEvent {
        REQUEST_ADD_TILE,
        REQUEST_PIN_WIDGET,
        EXIT
    }

    private final MutableLiveData<NavigationEvent> navigationEvent = new MutableLiveData<>();

    /**
     * Returns the LiveData for navigation events.
     * @return LiveData observed by the View.
     */
    public LiveData<NavigationEvent> getNavigationEvent() {
        return navigationEvent;
    }

    /**
     * Triggered when the user wants to request adding a tile to the system panel.
     */
    public void onRequestAddTile() {
        navigationEvent.setValue(NavigationEvent.REQUEST_ADD_TILE);
    }

    /**
     * Triggered when the user wants to request pinning a widget to the home screen.
     */
    public void onRequestPinWidget() {
        navigationEvent.setValue(NavigationEvent.REQUEST_PIN_WIDGET);
    }

    /**
     * Triggered when the user wants to exit the current page.
     */
    public void onExit() {
        navigationEvent.setValue(NavigationEvent.EXIT);
    }

    /**
     * Resets the navigation event after it has been handled by the View.
     */
    public void onNavigationHandled() {
        navigationEvent.setValue(null);
    }
}
