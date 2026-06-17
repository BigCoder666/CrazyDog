package com.example.datalibrary.deptrum;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 渲染
 */
public class GLFrameRenderer implements Renderer {
    private static final int SFPS = 25;

    private ISimplePlayer mParentAct;
    private GLSurfaceView mTargetSurface;
    private RGB24GLProgram rgbProg = new RGB24GLProgram(0);
    private YUV420PGLProgram yuvProg = new YUV420PGLProgram(0);
    private int mScreenWidth;
    private int mScreenHeight;
    private int mVideoWidth;
    private int mVideoHeight;
    private ByteBuffer y;
    private ByteBuffer u;
    private ByteBuffer v;
    private ByteBuffer blackUV;
    private ByteBuffer rgb24;

    private int mDisplayDegrees;
    private boolean mNeedMirror = false;
    private long mLastFrameTime = 0;
    private long mStanderDelta;
    private int dataType = 0; // 0:yuv420p 1:rgb24

    public GLFrameRenderer(GLSurfaceView surface) {
        // mParentAct = callback;
        mTargetSurface = surface;
        mDisplayDegrees = 0;
        mNeedMirror = false;
        mStanderDelta = 1000 / SFPS;
    }

    public void setResolution(int nWidth, int nHeight) {
        mScreenWidth = nWidth;
        mScreenHeight = nHeight;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Utils.LOGD("GLFrameRenderer :: onSurfaceCreated");
        if (!rgbProg.isProgramBuilt()) {
            rgbProg.buildProgram();
        }
        if (!yuvProg.isProgramBuilt()) {
            yuvProg.buildProgram();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Utils.LOGD("GLFrameRenderer :: onSurfaceChanged");
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (this) {
            if (mLastFrameTime == 0) {
                mLastFrameTime = System.currentTimeMillis();
            } else {
                long currentTime = System.currentTimeMillis();
                long delta = currentTime - mLastFrameTime;
                if (delta < mStanderDelta) {
                    try {
                        Thread.sleep(mStanderDelta - delta);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mLastFrameTime = currentTime;
            }
            switch (dataType) {
                case 0: {
                    // 绿帧则不渲染
                    if (y != null) {
                        if (isNullFrame(y, u, v)) {
                            return;
                        } else {
                            y.position(0);
                            u.position(0);
                            v.position(0);
                            yuvProg.buildTextures(y, u, v, mVideoWidth, mVideoHeight);
                            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
                            yuvProg.drawFrame();
                        }
                    }
                    break;
                }
                case 1: {
                    if (rgb24 != null) {
                        rgb24.position(0);
                        rgbProg.buildTextures(rgb24, mVideoWidth, mVideoHeight, false);
                        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
                        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                        rgbProg.drawFrame();
                    }
                    break;
                }
            }
        }
    }

    public void clear() {
        switch (dataType) {
            case 0: {
                if (y == null || u == null || v == null) {
                    return;
                }
                int size = this.mVideoWidth * this.mVideoHeight;
                byte[] b = new byte[size];
                initBlackUV();
                synchronized (this) {
                    y.clear();
                    u.clear();
                    v.clear();
                    y.put(b);
                    blackUV.position(0);
                    u.put(blackUV);
                    blackUV.position(0);
                    v.put(blackUV);
                }
                mTargetSurface.requestRender();
                break;
            }
            case 1: {
                rgb24.clear();
                rgb24.put(new byte[rgb24.capacity()]);
                mTargetSurface.requestRender();
                break;
            }
        }
    }

    /**
     * this method will be called from native code, it happens when the video is about to play or the
     * video size changes.
     */
    public void update(int w, int h, int dataType) {
        this.dataType = dataType;
        // Utils.LOGD("INIT E");
        if (w > 0 && h > 0) {

            if (mScreenWidth > 0 && mScreenHeight > 0) {
                float f1 = 1f * mScreenHeight / mScreenWidth;
                float f2 = 1f * h / w;
                if (f1 == f2) {
                    rgbProg.createBuffers(RGB24GLProgram.squareVertices);
                    yuvProg.createBuffers(YUV420PGLProgram.squareVertices);
                } else if (f1 < f2) {
                    float widScale = f1 / f2;
                    rgbProg.createBuffers(
                            new float[]{
                                    -widScale, -1.0f, widScale, -1.0f, -widScale, 1.0f, widScale, 1.0f,
                            });
                    yuvProg.createBuffers(
                            new float[]{
                                    -widScale, -1.0f, widScale, -1.0f, -widScale, 1.0f, widScale, 1.0f,
                            });
                } else {
                    float heightScale = f2 / f1;
                    rgbProg.createBuffers(
                            new float[]{
                                    -1.0f, -heightScale, 1.0f, -heightScale, -1.0f, heightScale, 1.0f, heightScale,
                            });
                    yuvProg.createBuffers(
                            new float[]{
                                    -1.0f, -heightScale, 1.0f, -heightScale, -1.0f, heightScale, 1.0f, heightScale,
                            });
                }
            }

            if (w != mVideoWidth && h != mVideoHeight) {
                this.mVideoWidth = w;
                this.mVideoHeight = h;
                int yarraySize = w * h;
                if (dataType == 0) {
                    int uvarraySize = yarraySize / 4;
                    synchronized (this) {
                        y = ByteBuffer.allocate(yarraySize);
                        u = ByteBuffer.allocate(uvarraySize);
                        v = ByteBuffer.allocate(uvarraySize);
                    }
                } else if (dataType == 1) {
                    synchronized (this) {
                        rgb24 = ByteBuffer.allocate(yarraySize * 3);
                    }
                }
            }
        }

        // mParentAct.onPlayStart();
        // Utils.LOGD("INIT X");
    }

    /**
     * this method will be called from native code, it's used for passing yuv data to me.
     */
    public void update(byte[] ydata, byte[] udata, byte[] vdata) {
        synchronized (this) {
            y.clear();
            u.clear();
            v.clear();
            y.put(ydata, 0, ydata.length);
            u.put(udata, 0, udata.length);
            v.put(vdata, 0, vdata.length);
        }
        // request to render
        mTargetSurface.requestRender();
    }

    /**
     * @param dataType 0:yuv420p 1:rgb24
     */
    public void update(byte[] data, int dataType) {
        this.dataType = dataType;
        int size = this.mVideoWidth * this.mVideoHeight;
        synchronized (this) {
            switch (this.dataType) {
                case 0: {
                    y.clear();
                    u.clear();
                    v.clear();
                    y.put(data, 0, size);
                    u.put(data, size, size / 4);
                    v.put(data, size * 5 / 4, size / 4);
                    break;
                }
                case 1: {
                    if (rgb24 == null) {
                        rgb24 = ByteBuffer.allocate(size * 3);
                    }
                    rgb24.clear();
                    rgb24.put(data, 0, size * 3);
                    break;
                }
            }
        }
        // request to render
        mTargetSurface.requestRender();
    }

    /*
     * 取图像四角与中心判定
     * */
    private boolean isNullFrame(ByteBuffer y, ByteBuffer u, ByteBuffer v) {
        switch (dataType) {
            case 0: {
                int size = mVideoWidth * mVideoHeight;
                if (y == null
                        || u == null
                        || v == null
                        || y.capacity() < size
                        || u.capacity() < size / 4
                        || v.capacity() < size / 4) {
                    return true;
                }
                int ypos = y.position();
                int upos = u.position();
                int vpos = v.position();
                int[] points = {0, mVideoWidth - 2, size / 2, size - mScreenWidth + 2, size - 2};
                int flag = 2; // 最少两点为绿则判断为绿帧

                for (int i = 0; i < 5; i++) {
                    if (y.get(points[i]) == 0 && u.get(points[i] / 4) == 0 && v.get(points[i] / 4) == 0) {
                        flag--;
                        if (flag <= 0) {
                            y.position(ypos);
                            u.position(upos);
                            v.position(vpos);
                            return true;
                        }
                    }
                }
                y.position(ypos);
                u.position(upos);
                v.position(vpos);
                return false;
            }
            case 1: {
                break;
            }
        }
        return false;
    }

    private void initBlackUV() {
        int size = mVideoWidth * mVideoHeight;
        if (blackUV == null || blackUV.capacity() < size / 4) {
            blackUV = ByteBuffer.allocate(size / 4);
            blackUV.position(0);
            for (int i = 0; i < size / 4; i++) {
                blackUV.put((byte) 128);
            }
            blackUV.position(0);
        } else {
            blackUV.position(0);
        }
    }

    /**
     * this method will be called from native code, it's used for passing play state to activity.
     */
    public void updateState(int state) {
        // Utils.LOGD("updateState E = " + state);
        if (mParentAct != null) {
            mParentAct.onReceiveState(state);
        }
        // Utils.LOGD("updateState X");
    }

    public void setDisplayOrientation(int displayOrientation) {
        mDisplayDegrees = displayOrientation;
        rgbProg.setDisplayOrientation(displayOrientation, mNeedMirror);
        yuvProg.setDisplayOrientation(displayOrientation, mNeedMirror);
    }

    public void displayMirror(boolean mirror) {
        mNeedMirror = mirror;
        rgbProg.setDisplayOrientation(mDisplayDegrees, mNeedMirror);
        yuvProg.setDisplayOrientation(mDisplayDegrees, mNeedMirror);
    }

    public void release() {
        rgbProg.releaseProgram();
        yuvProg.releaseProgram();
    }
}
