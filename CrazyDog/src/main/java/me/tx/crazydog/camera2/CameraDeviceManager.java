package me.tx.crazydog.camera2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.util.ArrayList;
import java.util.List;

/**
 * Core manager for Camera2. Handles opening/closing camera, creating capture session and preview
 * while forwarding frames via ImageReaderManager.
 */
public class CameraDeviceManager implements TextureView.SurfaceTextureListener { // 新增实现接口
	private static final String TAG = "CameraDeviceManager";

	private CameraManager mCameraManager;
	private CameraDevice mCameraDevice;
	private ImageReaderManager mImageReaderManager;
	private CameraCaptureSession mCaptureSession;

	private HandlerThread mBackgroundThread;
	private Handler mBackgroundHandler;

	private CaptureRequest.Builder mPreviewRequestBuilder;
	private CaptureRequest mPreviewRequest;

	private TextureView mPreviewTextureView;
	private Surface mPreviewSurface;

	public CameraDeviceManager() {
		mImageReaderManager = new ImageReaderManager();
	}

	public void initCameraManager(Context context) {
		if (context == null) return;
		mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
		startBackgroundThread();
	}

	public void setPreviewTextureView(TextureView textureView) {
		if (textureView == null) {
			Log.w(TAG, "setPreviewTextureView: textureView is null");
			return;
		}
		mPreviewTextureView = textureView;
		mPreviewTextureView.setSurfaceTextureListener(this);
		if (mPreviewTextureView.isAvailable()) {
			onSurfaceTextureAvailable(mPreviewTextureView.getSurfaceTexture(),
					mPreviewTextureView.getWidth(),
					mPreviewTextureView.getHeight());
		}
	}

	public String[] getCameraIdList() {
		if (mCameraManager == null) return new String[0];
		try {
			return mCameraManager.getCameraIdList();
		} catch (CameraAccessException e) {
			Log.e(TAG, "getCameraIdList failed", e);
			return new String[0];
		}
	}

	public CameraCharacteristics getCameraCharacteristics(String cameraId) {
		if (mCameraManager == null) return null;
		try {
			return mCameraManager.getCameraCharacteristics(cameraId);
		} catch (CameraAccessException e) {
			Log.e(TAG, "getCameraCharacteristics failed", e);
			return null;
		}
	}

	@SuppressLint("MissingPermission")
	public void openCamera(String cameraId) {
		if (mCameraManager == null) {
			Log.w(TAG, "CameraManager not initialized");
			return;
		}
		try {
			mCameraManager.openCamera(cameraId, mStateCallback, mBackgroundHandler);
		} catch (SecurityException e) {
			Log.e(TAG, "openCamera: missing permission?", e);
		} catch (CameraAccessException e) {
			Log.e(TAG, "openCamera failed", e);
		}
	}

	public void closeCamera() {
		try {
			if (mCaptureSession != null) {
				try { mCaptureSession.stopRepeating(); } catch (Exception ignored) {}
				try { mCaptureSession.abortCaptures(); } catch (Exception ignored) {}
				mCaptureSession.close();
				mCaptureSession = null;
			}
			if (mCameraDevice != null) {
				mCameraDevice.close();
				mCameraDevice = null;
			}
			if (mPreviewSurface != null) {
				mPreviewSurface.release();
				mPreviewSurface = null;
			}
		} catch (Exception e) {
			Log.e(TAG, "closeCamera error", e);
		}
	}

	public boolean createCaptureSession() {
		if (mCameraDevice == null) {
			Log.w(TAG, "createCaptureSession: cameraDevice is null");
			return false;
		}
		Surface imageReaderSurface = mImageReaderManager.getSurface();
		if (imageReaderSurface == null) {
			Log.w(TAG, "createCaptureSession: imageReader surface is null");
			return false;
		}
		if (mPreviewSurface == null) {
			Log.w(TAG, "createCaptureSession: preview surface is null");
			return false;
		}

		try {
			List<Surface> surfaces = new ArrayList<>();
			surfaces.add(imageReaderSurface);
			surfaces.add(mPreviewSurface);
			mCameraDevice.createCaptureSession(surfaces, mSessionCallback, mBackgroundHandler);
		} catch (CameraAccessException e) {
			Log.e(TAG, "createCaptureSession failed", e);
			return false;
		}
		return true;
	}

	public boolean startPreview() {
		if (mCameraDevice == null) {
			Log.w(TAG, "startPreview: cameraDevice is null");
			return false;
		}
		if (mPreviewSurface == null) {
			Log.w(TAG, "startPreview: preview surface not ready");
			return false;
		}
		createCaptureSession();
		return true;
	}

	public void stopPreview() {
		if (mCaptureSession != null) {
			try {
				mCaptureSession.stopRepeating();
			} catch (Exception e) {
				Log.w(TAG, "stopPreview issue", e);
			}
		}
	}

	public void releaseCamera() {
		closeCamera();
		stopBackgroundThread();
		if (mImageReaderManager != null) {
			mImageReaderManager.releaseImageReader();
			mImageReaderManager = null;
		}
		mPreviewTextureView = null;
	}

	public ImageReaderManager getImageReaderManager() {
		return mImageReaderManager;
	}

	private void startBackgroundThread() {
		if (mBackgroundThread != null) return;
		mBackgroundThread = new HandlerThread("CameraBackground");
		mBackgroundThread.start();
		mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
	}

	private void stopBackgroundThread() {
		if (mBackgroundThread == null) return;
		try {
			mBackgroundThread.quitSafely();
			mBackgroundThread.join();
		} catch (InterruptedException e) {
			Log.w(TAG, "stopBackgroundThread interrupted", e);
		}
		mBackgroundThread = null;
		mBackgroundHandler = null;
	}

	private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
		@Override
		public void onOpened(CameraDevice camera) {
			mCameraDevice = camera;
			Log.d(TAG, "camera opened");
		}

		@Override
		public void onDisconnected(CameraDevice camera) {
			Log.w(TAG, "camera disconnected");
			camera.close();
			mCameraDevice = null;
		}

		@Override
		public void onError(CameraDevice camera, int error) {
			Log.e(TAG, "camera error: " + error);
			camera.close();
			mCameraDevice = null;
		}
	};

	private final CameraCaptureSession.StateCallback mSessionCallback = new CameraCaptureSession.StateCallback() {
		@Override
		public void onConfigured(CameraCaptureSession session) {
			if (mCameraDevice == null) return;
			mCaptureSession = session;
			try {
				Surface imageReaderSurface = mImageReaderManager.getSurface();
				mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
				mPreviewRequestBuilder.addTarget(mPreviewSurface);
				mPreviewRequestBuilder.addTarget(imageReaderSurface);
				mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
				mPreviewRequest = mPreviewRequestBuilder.build();
				mCaptureSession.setRepeatingRequest(mPreviewRequest, null, mBackgroundHandler);
				Log.d(TAG, "capture session configured and repeating request started");
			} catch (CameraAccessException e) {
				Log.e(TAG, "onConfigured error", e);
			}
		}

		@Override
		public void onConfigureFailed(CameraCaptureSession session) {
			Log.e(TAG, "capture session configuration failed");
		}
	};

	public void setFrameCallback(Camera2FrameCallback.FrameListener listener) {
		if (mImageReaderManager == null) return;
		Camera2FrameCallback callback = new Camera2FrameCallback();
		callback.setFrameListener(listener);
		mImageReaderManager.setOnImageAvailableListener(callback, mBackgroundHandler);
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
		Log.d(TAG, "onSurfaceTextureAvailable: width=" + width + ", height=" + height);
		mPreviewSurface = new Surface(surfaceTexture);
		if (mCameraDevice != null) {
			createCaptureSession();
		}
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
		Log.d(TAG, "onSurfaceTextureSizeChanged: width=" + width + ", height=" + height);
		stopPreview();
		if (mCameraDevice != null) {
			createCaptureSession();
		}
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
		Log.d(TAG, "onSurfaceTextureDestroyed");
		if (mPreviewSurface != null) {
			mPreviewSurface.release();
			mPreviewSurface = null;
		}
		stopPreview();
		return true;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

	}
}