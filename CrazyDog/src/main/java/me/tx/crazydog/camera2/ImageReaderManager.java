package me.tx.crazydog.camera2;

import android.graphics.Bitmap;
import android.media.ImageReader;
import android.util.Log;
import android.view.Surface;

/**
 * Manages an ImageReader configured for YUV_420_888 and forwards frames to Camera2FrameCallback.
 */
public class ImageReaderManager {
	private static final String TAG = "ImageReaderManager";

	private ImageReader mImageReader;
	private Camera2FrameCallback mFrameCallback;

	public Bitmap getLastBitmap(){
		if(mFrameCallback!=null){
			return mFrameCallback.nv21ToBitmap(mFrameCallback.lastNv21Data,mFrameCallback.lastwidth,mFrameCallback.lastheight);
		}
		return null;
	}

	public ImageReaderManager() {
	}

	/**
	 * Initialize the ImageReader for given size. Format is YUV_420_888.
	 */
	public void initImageReader(int width, int height) {
		if (mImageReader != null) {
			releaseImageReader();
		}
		mImageReader = ImageReader.newInstance(width, height, android.graphics.ImageFormat.YUV_420_888, 2);
	}

	public Surface getSurface() {
		if (mImageReader == null) return null;
		return mImageReader.getSurface();
	}

	/**
	 * Set a Camera2FrameCallback and attach it to the ImageReader.
	 */
	public void setOnImageAvailableListener(Camera2FrameCallback callback, android.os.Handler handler) {
		if (mImageReader == null) {
			Log.w(TAG, "setOnImageAvailableListener called before initImageReader");
			return;
		}
		this.mFrameCallback = callback;
		mImageReader.setOnImageAvailableListener(mFrameCallback, handler);
	}

	/**
	 * Release the ImageReader resources
	 */
	public void releaseImageReader() {
		if (mImageReader != null) {
			try {
				mImageReader.setOnImageAvailableListener(null, null);
			} catch (Exception ignored) {
			}
			Surface s = mImageReader.getSurface();
			if (s != null) {
				try { s.release(); } catch (Exception ignored) {}
			}
			mImageReader.close();
			mImageReader = null;
		}
		mFrameCallback = null;
	}
}
