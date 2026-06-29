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

package com.adam.app.demoset.graphics;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * A simple OpenGL ES 3.0 renderer that draws a rotating 3D cube.
 */
public class CubeRenderer implements GLSurfaceView.Renderer {

    private static final String VERTEX_SHADER =
            "#version 300 es\n" +
            "layout(location = 0) in vec4 vPosition;\n" +
            "layout(location = 1) in vec4 vColor;\n" +
            "uniform mat4 uMVPMatrix;\n" +
            "out vec4 vVaryingColor;\n" +
            "void main() {\n" +
            "  gl_Position = uMVPMatrix * vPosition;\n" +
            "  vVaryingColor = vColor;\n" +
            "}\n";

    private static final String FRAGMENT_SHADER =
            "#version 300 es\n" +
            "precision mediump float;\n" +
            "in vec4 vVaryingColor;\n" +
            "out vec4 fragColor;\n" +
            "void main() {\n" +
            "  fragColor = vVaryingColor;\n" +
            "}\n";

    private final FloatBuffer mCubeBuffer;
    private final FloatBuffer mColorBuffer;
    private final ByteBuffer mIndexBuffer;

    private int mProgram;
    private int mMatrixHandle;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    private boolean mIsAnimating = false;

    public CubeRenderer() {
        // Cube vertices
        float[] cubeCoords = {
                -0.5f,  0.5f,  0.5f,   // top left front
                -0.5f, -0.5f,  0.5f,   // bottom left front
                 0.5f, -0.5f,  0.5f,   // bottom right front
                 0.5f,  0.5f,  0.5f,   // top right front
                -0.5f,  0.5f, -0.5f,   // top left back
                -0.5f, -0.5f, -0.5f,   // bottom left back
                 0.5f, -0.5f, -0.5f,   // bottom right back
                 0.5f,  0.5f, -0.5f    // top right back
        };

        // Cube colors (RGBA)
        float[] cubeColors = {
                1, 0, 0, 1, // red
                0, 1, 0, 1, // green
                0, 0, 1, 1, // blue
                1, 1, 0, 1, // yellow
                1, 0, 1, 1, // magenta
                0, 1, 1, 1, // cyan
                1, 1, 1, 1, // white
                0, 0, 0, 1  // black
        };

        // Indices to draw cube triangles
        byte[] indices = {
                0, 1, 2, 0, 2, 3, // front
                4, 5, 6, 4, 6, 7, // back
                0, 4, 7, 0, 7, 3, // top
                1, 5, 6, 1, 6, 2, // bottom
                0, 4, 5, 0, 5, 1, // left
                3, 7, 6, 3, 6, 2  // right
        };

        mCubeBuffer = ByteBuffer.allocateDirect(cubeCoords.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeBuffer.put(cubeCoords).position(0);

        mColorBuffer = ByteBuffer.allocateDirect(cubeColors.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColorBuffer.put(cubeColors).position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices).position(0);
    }

    public void setAnimating(boolean animating) {
        mIsAnimating = animating;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);

        mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShader);
        GLES30.glAttachShader(mProgram, fragmentShader);
        GLES30.glLinkProgram(mProgram);

        mMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -5, 0, 0, 0, 0, 1, 0);

        long time = mIsAnimating ? SystemClock.uptimeMillis() % 4000L : 0;
        float angle = 0.090f * (int) time;
        Matrix.setRotateM(mRotationMatrix, 0, angle, 1.0f, 1.0f, 0.5f);

        float[] scratch = new float[16];
        Matrix.multiplyMM(scratch, 0, mViewMatrix, 0, mRotationMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, scratch, 0);

        GLES30.glUseProgram(mProgram);

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, mCubeBuffer);

        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 0, mColorBuffer);

        GLES30.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 36, GLES30.GL_UNSIGNED_BYTE, mIndexBuffer);

        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        return shader;
    }
}
