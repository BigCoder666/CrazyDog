package com.example.datalibrary.deptrum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.RelativeLayout;

import com.example.datalibrary.utils.ImageUtils;

public class MantleGLFrameSurface extends RelativeLayout {
    private boolean isDraw = false;

    private int drawLength = 200;
    public float circleRadius;
    public float circleX;
    public float circleY;
    private Context context;
    public TextureView textureView;
    private TextureView faceTexture;
    protected GLFrameSurface mRgbSurface;

    private int videoWidth = 0;
    private int videoHeight = 0;
    public int previewWidth = 0;
    private int previewHeight = 0;
    private static int scale = 2;

    private boolean mIsRegister;   // 注册

    public void setDraw(boolean draw) {
        isDraw = draw;
        postInvalidate();
    }
    public boolean isDraw() {
        return isDraw;
    }
    public GLFrameSurface getGLFrameSurface() {
        return mRgbSurface;
    }

    public MantleGLFrameSurface(Context context) {
        super(context);
        init(context);
    }

    public MantleGLFrameSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MantleGLFrameSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MantleGLFrameSurface(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    public void setDrawHeightLength(int drawLength) {
        this.drawLength = drawLength;
    }
    private void init(Context context){
        this.context = getContext();
        setWillNotDraw(false);
    }
    // 初始化TextureG
    public void initSurface(){
        mRgbSurface = new GLFrameSurface(context);
        addView(mRgbSurface);
    }
    private void setTextureLayout(){
        if (videoWidth == 0 || videoHeight == 0 || previewWidth == 0 || previewHeight == 0 || textureView == null) {
            return;
        }

        if (previewWidth * videoHeight > previewHeight * videoWidth) {
            int scaledChildHeight = videoHeight * previewWidth / videoWidth;
            textureView.layout(0, (previewHeight - scaledChildHeight) / scale,
                    previewWidth, (previewHeight + scaledChildHeight) / scale);
            faceTexture.layout(0, (previewHeight - scaledChildHeight) / scale,
                    previewWidth, (previewHeight + scaledChildHeight) / scale);
        } else {
            int scaledChildWidth = videoWidth * previewHeight / videoHeight;
            textureView.layout((previewWidth - scaledChildWidth) / scale, 0,
                    (previewWidth + scaledChildWidth) / scale, previewHeight);
            faceTexture.layout((previewWidth - scaledChildWidth) / scale, 0,
                    (previewWidth + scaledChildWidth) / scale, previewHeight);

        }
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        previewWidth = getWidth();
        previewHeight = getHeight();
        setTextureLayout();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if (isDraw) {
            Path path = new Path();
            // 设置裁剪的圆心坐标，半径
            path.addCircle(getWidth() / 2,
                    (getHeight() - ImageUtils.dip2px(context , drawLength)) / 2, getWidth() / 3, Path.Direction.CCW);
            // 裁剪画布，并设置其填充方式
            // canvas.clipPath(path, Region.Op.REPLACE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                canvas.clipPath(path);
            } else {
                canvas.clipPath(path, Region.Op.REPLACE); // REPLACE、UNION 等
            }
            // 圆的半径
            circleRadius = getWidth() / 3;
            // 圆心的X坐标
            circleX = (getRight() - getLeft()) / 2;
            // 圆心的Y坐标
            circleY = (getBottom() - getTop()) / 2 ;
        }

        if (mIsRegister) {
            Path path = new Path();
            // 设置裁剪的圆心坐标，半径
            path.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 3, Path.Direction.CCW);
            // 裁剪画布，并设置其填充方式
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                canvas.clipPath(path);
            } else {
                canvas.clipPath(path, Region.Op.REPLACE); // REPLACE、UNION 等
            }
            // 圆的半径
            circleRadius = getWidth() / 3;
            // 圆心的X坐标
            circleX = (getRight() - getLeft()) / 2;
            // 圆心的Y坐标
            circleY = (getBottom() - getTop()) / 2;
        }
        super.onDraw(canvas);
    }

    public void setIsRegister(boolean isRegister) {
        mIsRegister = isRegister;
        invalidate();
    }
    public TextureView getTextureView() {
        return textureView;
    }
    private Handler handler = new Handler(Looper.getMainLooper());
    public void setPreviewSize(int width, int height) {
        if (this.videoWidth == width && this.videoHeight == height) {
            return;
        }
        this.videoWidth = width;
        this.videoHeight = height;
        handler.post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });

    }


}
