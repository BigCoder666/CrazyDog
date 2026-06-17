package com.example.datalibrary.gatecamera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;

import com.example.datalibrary.callback.CameraDataCallback;
import com.example.datalibrary.gl.view.GLFaceSurfaceView;
import com.example.datalibrary.gl.view.GlMantleSurfacView;

import java.io.IOException;
import java.util.List;

/**
 * Time: 2019/1/24
 * Author: v_chaixiaogang
 * Description:
 */
public class CameraPreviewManager implements SurfaceHolder.Callback , TextureView.SurfaceTextureListener {

    private static final String TAG = "camera_preview";


    GLFaceSurfaceView surfaceView;
    boolean mPreviewed = false;

    public static final int CAMERA_FACING_BACK = 0;

    public static final int CAMERA_FACING_FRONT = 1;

    public static final int CAMERA_USB = 2;

    /**
     * 当前相机的ID。
     */
    private int cameraFacing = CAMERA_FACING_BACK;

    private int previewWidth;
    private int previewHeight;

    private int videoWidth;
    private int videoHeight;

    private int surfaceWidth;
    private int surfaceHeight;

    private Camera mCamera;

    private int displayOrientation = 0;

    private int videoDirection;

    private GlMantleSurfacView mTextureView;
    private SurfaceTexture mSurfaceTexture;
    private int mirrorVideoRGB;
    public void setmCameraDataCallback(CameraDataCallback mCameraDataCallback) {
        this.mCameraDataCallback = mCameraDataCallback;
    }

    private CameraDataCallback mCameraDataCallback;
    private static volatile CameraPreviewManager instance = null;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {

        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    public static CameraPreviewManager getInstance() {
        synchronized (CameraPreviewManager.class) {
            if (instance == null) {
                instance = new CameraPreviewManager();
            }
        }
        return instance;
    }

    public int getCameraFacing() {
        return cameraFacing;
    }

    public void setCameraFacing(int cameraFacing) {
        this.cameraFacing = cameraFacing;
    }

    public int getDisplayOrientation() {
        return displayOrientation;
    }

    public void setDisplayOrientation(int displayOrientation) {
        this.displayOrientation = displayOrientation;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, final int width, final int height) {
        if (surfaceView != null){
            surfaceWidth = width;
            surfaceHeight = height;
            surfaceView.surfaceChanged(surfaceHolder);

            openCamera();
        }
    }
    /**
     * 开启预览
     * @param textureView
     */
    public void startPreview(GlMantleSurfacView textureView, int videoDirection ,
                             int width, int height) {
        Log.e(TAG, "开启预览模式");
        mTextureView = textureView;
        if (textureView.getGlFaceSurfaceView() != null){
            surfaceView = textureView.getGlFaceSurfaceView();
            this.previewWidth = width;
            this.previewHeight = height;
            this.videoDirection = videoDirection;
        }else {
            this.previewWidth = width;
            this.previewHeight = height;
            this.videoDirection = videoDirection;
            this.mirrorVideoRGB = textureView.getMirrorRGB();
            mSurfaceTexture = mTextureView.getTextureView().getSurfaceTexture();
            mTextureView.getTextureView().setSurfaceTextureListener(this);
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (surfaceView != null){
            surfaceView.surfaceDestroyed();
        }
    }

    /**
     * 关闭预览
     */
    public void stopPreview() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mPreviewed = false;
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                Log.e("qing", "camera destory error");
                e.printStackTrace();

            }
        }
    }

    public int[] initCamera(){
        try {
            if (mCamera == null) {
                mCamera = Camera.open(cameraFacing);
                Log.e(TAG, "initCamera---open camera");
            }
            // 摄像头图像预览角度
            int cameraRotation = videoDirection;
            mCamera.setDisplayOrientation(cameraRotation);
            if (mTextureView != null){
                if (cameraRotation == 90 || cameraRotation == 270) {
                    if (mirrorVideoRGB == 1) {
                        mTextureView.setRotationY(180);
                    } else {
                        mTextureView.setRotationY(0);
                    }
                    // 旋转90度或者270，需要调整宽高
                    mTextureView.setPreviewSize(previewHeight, previewWidth);
                } else {
                    if (mirrorVideoRGB == 1) {
                        mTextureView.setRotationY(180);
                    } else {
                        mTextureView.setRotationY(0);
                    }
                    mTextureView.setPreviewSize(previewWidth, previewHeight);
                }
            }
            Camera.Parameters params = mCamera.getParameters();
            List<Camera.Size> sizeList = params.getSupportedPreviewSizes(); // 获取所有支持的camera尺寸
            final Camera.Size optionSize = getOptimalPreviewSize(sizeList, previewWidth,
                    previewHeight); // 获取一个最为适配的camera.size
            if (optionSize.width == previewWidth && optionSize.height == previewHeight) {
                videoWidth = previewWidth;
                videoHeight = previewHeight;
            } else {
                videoWidth = optionSize.width;
                videoHeight = optionSize.height;
            }
            params.setPreviewSize(videoWidth, videoHeight);

            mCamera.setParameters(params);
            if (mTextureView != null){
                try {
                    mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                        @Override
                        public void onPreviewFrame(byte[] bytes, Camera camera) {
                            if (mCameraDataCallback != null) {
                                mCameraDataCallback.onGetCameraData(bytes, camera,
                                        videoWidth, videoHeight);
                            }
                        }
                    });
                    mCamera.setPreviewTexture(mSurfaceTexture);

                    mCamera.startPreview();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
            if (surfaceView != null){
                surfaceView.getHolder().addCallback(this);
                surfaceView.setWillNotDraw(false);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return new int[]{videoWidth , videoHeight};
    }
    /**
     * 开启摄像头
     */
    public void openCamera() {

        try {
            try {
                mCamera.setPreviewTexture(surfaceView.getFramebuffer());
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] bytes, Camera camera) {
                        if (mCameraDataCallback != null) {
                            mCameraDataCallback.onGetCameraData(bytes, camera,
                                    videoWidth, videoHeight);
                        }
                    }
                });
                mCamera.startPreview();

                surfaceView.initFrame(surfaceWidth , surfaceHeight);
                if (videoDirection == 90 || videoDirection == 270){
                    surfaceView.setSize(videoHeight , videoWidth);
                }else {
                    surfaceView.setSize(videoWidth , videoHeight);

                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        } catch (RuntimeException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    private int getCameraDisplayOrientation(int degrees, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation + degrees) % 360;
            rotation = (360 - rotation) % 360;
        } else { // back-facing
            rotation = (info.orientation - degrees + 360) % 360;
        }
        return rotation;
    }


    /**
     * 解决预览变形问题
     *
     * @param sizes
     * @param w
     * @param h
     * @return
     */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double aspectTolerance = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) {
            return null;
        }
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {

            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > aspectTolerance) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture texture, int i, int i1) {
        try {
            if (mCamera != null && !mPreviewed) {
                mSurfaceTexture = texture;
                mCamera.setPreviewTexture(mSurfaceTexture);
                mCamera.startPreview();
                mPreviewed = true;
            }
        } catch (Exception exception) {
            Log.e("chaixiaogang", "IOException caused by setPreviewDisplay()", exception);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.e(TAG, "--surfaceTexture--TextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (mCamera != null) {
            // mCamera.stopPreview();
            mPreviewed = false;
//            mCamera.setPreviewCallback(null);
//            mCamera.stopPreview();
//            mCamera.release();
//            mCamera = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        Log.e(TAG, "--surfaceTexture--Updated");
    }
}