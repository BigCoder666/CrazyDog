package me.tx.crazydog.camera2;

import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Callback that receives ImageReader frames, converts YUV_420_888 to NV21 and forwards
 * the byte[] to a listener.
 */
public class Camera2FrameCallback implements ImageReader.OnImageAvailableListener {
	private static final String TAG = "Camera2FrameCallback";

	public interface FrameListener {
		void onFrameResult(byte[] nv21Data, int width, int height);
	}

	private FrameListener mListener;

	public void setFrameListener(FrameListener listener) {
		this.mListener = listener;
	}

	@Override
	public void onImageAvailable(ImageReader reader) {
		Image image = null;
		try {
			image = reader.acquireLatestImage();
			if (image == null) return;

			byte[] nv21 = getNV21DataFromImage(image);
			if (nv21 != null && mListener != null) {
				mListener.onFrameResult(nv21, image.getWidth(), image.getHeight());
			}
		} catch (Exception e) {
			Log.e(TAG, "onImageAvailable error", e);
		} finally {
			if (image != null) image.close();
		}
	}

	/**
	 * Convert YUV_420_888 Image to NV21 byte[]
	 */
	public static byte[] getNV21DataFromImage(Image image) {
		if (image == null) return null;

		final int width = image.getWidth();
		final int height = image.getHeight();

		Image.Plane[] planes = image.getPlanes();
		ByteBuffer yBuffer = planes[0].getBuffer(); // Y
		ByteBuffer uBuffer = planes[1].getBuffer(); // U (Cb)
		ByteBuffer vBuffer = planes[2].getBuffer(); // V (Cr)

		int ySize = yBuffer.remaining();
		int uSize = uBuffer.remaining();
		int vSize = vBuffer.remaining();

		byte[] nv21 = new byte[ySize + uSize + vSize];

		// Copy Y
		yBuffer.get(nv21, 0, ySize);

		// NV21 requires VU interleaved
		// U and V planes may have different pixelStride/rowStride; we must handle that
		int chromaHeight = height / 2;
		int chromaWidth = width / 2;

		int offset = ySize;

		int uPixelStride = planes[1].getPixelStride();
		int uRowStride = planes[1].getRowStride();
		int vPixelStride = planes[2].getPixelStride();
		int vRowStride = planes[2].getRowStride();

		byte[] uRow = new byte[uRowStride];
		byte[] vRow = new byte[vRowStride];

		// Iterate through chroma rows
		for (int row = 0; row < chromaHeight; row++) {
			int uRowStart = row * uRowStride;
			int vRowStart = row * vRowStride;

			// Read a whole row from each plane into temporary arrays
			// Note: using get() with position adjustments
			int uPos = uRowStart;
			int vPos = vRowStart;

			// Fill temporary arrays from underlying buffers
			// Duplicate buffers to avoid changing position on original
			ByteBuffer uBuf = planes[1].getBuffer().duplicate();
			ByteBuffer vBuf = planes[2].getBuffer().duplicate();
			uBuf.position(uRowStart);
			vBuf.position(vRowStart);
			int uLen = Math.min(uRowStride, uBuf.remaining());
			int vLen = Math.min(vRowStride, vBuf.remaining());
			uBuf.get(uRow, 0, uLen);
			vBuf.get(vRow, 0, vLen);

			for (int col = 0; col < chromaWidth; col++) {
				int uIndex = col * uPixelStride;
				int vIndex = col * vPixelStride;

				// NV21 is V then U
				if (vIndex < vRow.length) nv21[offset++] = vRow[vIndex];
				else nv21[offset++] = 0;
				if (uIndex < uRow.length) nv21[offset++] = uRow[uIndex];
				else nv21[offset++] = 0;
			}
		}

		return nv21;
	}
}
