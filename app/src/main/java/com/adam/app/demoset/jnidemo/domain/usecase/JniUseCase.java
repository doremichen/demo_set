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

package com.adam.app.demoset.jnidemo.domain.usecase;

import com.adam.app.demoset.jnidemo.data.JniRepositoryImpl;
import com.adam.app.demoset.jnidemo.domain.repository.JniRepository;

/**
 * JniUseCase - An enumeration that packages all JNI-related use cases.
 * 
 * Adhering to GRASP Principles:
 * 1. Creator: This enum is responsible for creating the JniRepository instance.
 * 2. Information Expert: It holds the repository and knows how to execute native actions.
 */
public enum JniUseCase {
    
    /**
     * Use case to get a greeting from native layer.
     */
    GET_HELLO {
        @Override
        public Object execute(Object... args) {
            return REPOSITORY.getHello();
        }
    },

    /**
     * Use case to trigger an instance-level callback.
     */
    TRIGGER_OBJECT_CALLBACK {
        @Override
        public Object execute(Object... args) {
            REPOSITORY.triggerObjectCallback();
            return null;
        }
    },

    /**
     * Use case to trigger a class-level callback.
     */
    TRIGGER_CLASS_CALLBACK {
        @Override
        public Object execute(Object... args) {
            REPOSITORY.triggerClassCallback();
            return null;
        }
    },

    /**
     * Use case to reset instance data.
     */
    CLEAR_OBJECT {
        @Override
        public Object execute(Object... args) {
            REPOSITORY.clearObjectData();
            return null;
        }
    },

    /**
     * Use case to reset class data.
     */
    CLEAR_CLASS {
        @Override
        public Object execute(Object... args) {
            REPOSITORY.clearClassData();
            return null;
        }
    },

    /**
     * Use case to perform native addition.
     * Expects args[0] (int) and args[1] (int).
     */
    PERFORM_CALCULATION {
        @Override
        public Object execute(Object... args) {
            if (args.length < 2) return 0;
            return REPOSITORY.calculateSum((int) args[0], (int) args[1]);
        }
    },

    /**
     * Use case to fetch system architecture info.
     */
    GET_SYSTEM_INFO {
        @Override
        public Object execute(Object... args) {
            return REPOSITORY.getNativeSystemInfo();
        }
    };

    /**
     * Singleton Repository instance.
     * JniUseCase acts as the 'Information Expert' and 'Creator' for the repository.
     */
    private static final JniRepository REPOSITORY = new JniRepositoryImpl();

    /**
     * Common execution method for all packaged use cases.
     * 
     * @param args Optional arguments for the specific use case.
     * @return Result of the operation, if any.
     */
    public abstract Object execute(Object... args);
}
