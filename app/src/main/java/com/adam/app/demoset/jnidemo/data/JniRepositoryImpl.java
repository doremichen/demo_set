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

package com.adam.app.demoset.jnidemo.data;

import com.adam.app.demoset.jnidemo.NativeUtils;
import com.adam.app.demoset.jnidemo.domain.repository.JniRepository;

/**
 * JniRepositoryImpl - Data layer implementation using the NativeUtils bridge.
 */
public class JniRepositoryImpl implements JniRepository {
    
    private final NativeUtils nativeUtils;

    public JniRepositoryImpl() {
        // Initialize bridge via Singleton pattern
        this.nativeUtils = NativeUtils.newInstance();
    }

    @Override
    public String getHello() {
        return nativeUtils.sayHello();
    }

    @Override
    public void triggerObjectCallback() {
        nativeUtils.objectCallBack();
    }

    @Override
    public void triggerClassCallback() {
        NativeUtils.classCallBack();
    }

    @Override
    public void clearObjectData() {
        nativeUtils.clearObjData();
    }

    @Override
    public void clearClassData() {
        NativeUtils.clearClazzData();
    }

    @Override
    public int calculateSum(int a, int b) {
        return nativeUtils.calculate(a, b);
    }

    @Override
    public String getNativeSystemInfo() {
        return nativeUtils.getSystemInfo();
    }
}
