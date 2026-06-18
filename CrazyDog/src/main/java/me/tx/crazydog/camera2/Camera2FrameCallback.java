package me.tx.crazydog.camera2;

import android.graphics.Bitmap;
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

	public abstract static class FrameListener {
		int drop_frame_limit = 0;
		int drop_frame_limit_count = 0;


		public FrameListener(int drop_frame_limit){
			this.drop_frame_limit = drop_frame_limit;
		}

		public boolean skip(){
			drop_frame_limit_count++;
			if(drop_frame_limit_count>=drop_frame_limit){
				drop_frame_limit_count = 0;
				return false;
			}else {
				return true;
			}
		}
		public abstract void onFrameResult(byte[] nv21Data, int width, int height);
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

			if(mListener!=null && !mListener.skip()) {
				byte[] nv21 = getNV21DataFromImage(image);
				if (nv21 != null) {
					mListener.onFrameResult(nv21, image.getWidth(), image.getHeight());
				}
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

	/**
	 * 直接用你已经拿到的 nv21 转 Bitmap，无JPEG，开销极低
	 */
	public static Bitmap nv21ToBitmap(byte[] nv21, int width, int height) {
		int[] argb = new int[width * height];
		decodeYUV420SP(argb, nv21, width, height);
		return Bitmap.createBitmap(argb, width, height, Bitmap.Config.ARGB_8888);
	}

	/**
	 * YUV NV21 转 ARGB（高速算法）
	 */
	private static void decodeYUV420SP(int[] rgba, byte[] yuv420sp, int width, int height) {
		final int frameSize = width * height;

		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				// Y
				int y = 0xff & yuv420sp[j * width + i];
				// U/V 位置
				int uvp = frameSize + (j >> 1) * width + (i & ~1);
				int v = 0xff & yuv420sp[uvp];
				int u = 0xff & yuv420sp[uvp + 1];

				y = (y - 16) * 1164;
				u -= 128;
				v -= 128;

				int r = (y + 1596 * v) / 1000;
				int g = (y -  813 * v - 392 * u) / 1000;
				int b = (y + 2018 * u) / 1000;

				r = r < 0 ? 0 : r > 255 ? 255 : r;
				g = g < 0 ? 0 : g > 255 ? 255 : g;
				b = b < 0 ? 0 : b > 255 ? 255 : b;

				rgba[j * width + i] = 0xff000000 | (r << 16) | (g << 8) | b;
			}
		}
	}
}
