package com.example.datalibrary.gl.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.RelativeLayout;

import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.utils.ImageUtils;
import com.example.datalibrary.view.BdFaceRectView;

import java.util.List;

public class GlMantleSurfacView extends RelativeLayout {
    private boolean isDraw = false;

    private int radius = 400;
    public float circleRadius;
    public float circleX;
    public float circleY;
    private Context context;
    private Paint paint;
    public TextureView textureView;
    private BdFaceRectView faceRectView;
    GLFaceSurfaceView glFaceSurfaceView;

    private int videoWidth = 0;
    private int videoHeight = 0;
    public int previewWidth = 0;
    public int previewHeight = 0;
    private static int scale = 2;

    private boolean mIsRegister;   // 注册

    private boolean rgbRevert; // 检测框镜像

    private int mirrorRGB; // 摄像头展示镜像

    private boolean isMultiIdentify = false;

    public void setMultiIdentify(boolean multiIdentify){
        this.isMultiIdentify = multiIdentify;
    }

    public void setDraw(boolean draw) {
        isDraw = draw;
        postInvalidate();
    }
    public boolean isDraw() {
        return isDraw;
    }
    public GLFaceSurfaceView getGlFaceSurfaceView() {
        return glFaceSurfaceView;
    }

    public GlMantleSurfacView(Context context) {
        super(context);
        init(context);
    }

    public GlMantleSurfacView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GlMantleSurfacView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public GlMantleSurfacView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    public void onGlDraw(){
        if (glFaceSurfaceView != null){
            glFaceSurfaceView.onGlDraw();
        }
    }

    public void onGlDraw(List<LivenessModel> models , List<LivenessModel> lastModels , float identifyThreshold) {
        if (glFaceSurfaceView != null){
            glFaceSurfaceView.onGlDraw(models , rgbRevert , isDraw || mIsRegister);
        } else {
            faceRectView.drawFaceRect(models , lastModels , identifyThreshold);
        }
    }
    private void init(Context context){
        this.context = getContext();
        setWillNotDraw(false);
    }
    // 初始化OpenGl
    public void setFrame(){
        if (glFaceSurfaceView == null){
            return;
        }
        glFaceSurfaceView.setFrame();
    }
    // 初始化TextureG
    public void initSurface(Boolean rgbRevert , int mirrorRGB , boolean isOpenGl){
        this.rgbRevert = rgbRevert;
        this.mirrorRGB = mirrorRGB;
        if (isOpenGl){
            glFaceSurfaceView = new GLFaceSurfaceView(context);
            addView(glFaceSurfaceView);
            glFaceSurfaceView.init(mirrorRGB);
        }else {

            textureView = new TextureView(getContext());
            faceRectView = new BdFaceRectView(getContext());
            faceRectView.setKeepScreenOn(true);
            faceRectView.setBackgroundColor(Color.TRANSPARENT);
            if (rgbRevert){
                faceRectView.setRotationY(180);
            }
            paint = new Paint();
            addView(textureView);
            addView(faceRectView);
        }
    }
    private void setTextureLayout(){
        if (videoWidth == 0 || videoHeight == 0 || previewWidth == 0 || previewHeight == 0 || textureView == null) {
            return;
        }

        if (previewWidth * videoHeight > previewHeight * videoWidth) {
            int scaledChildHeight = videoHeight * previewWidth / videoWidth;
            textureView.layout(0, (previewHeight - scaledChildHeight) / scale,
                    previewWidth, (previewHeight + scaledChildHeight) / scale);
            faceRectView.layout(0, (previewHeight - scaledChildHeight) / scale,
                    previewWidth, (previewHeight + scaledChildHeight) / scale);
        } else {
            int scaledChildWidth = videoWidth * previewHeight / videoHeight;
            textureView.layout((previewWidth - scaledChildWidth) / scale, 0,
                    (previewWidth + scaledChildWidth) / scale, previewHeight);
            faceRectView.layout((previewWidth - scaledChildWidth) / scale, 0,
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
            path.addCircle((float) getWidth() / 2,
                  (float) getHeight() / 2, (float) ImageUtils.px2dip(context, radius) / 2/*getWidth() / 3*/, Path.Direction.CCW);
            // 裁剪画布，并设置其填充方式
            // canvas.clipPath(path, Region.Op.REPLACE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                canvas.clipPath(path);
            } else {
                canvas.clipPath(path, Region.Op.REPLACE); // REPLACE、UNION 等
            }
            // 圆的半径
            circleRadius = (float) ImageUtils.px2dip(context , radius);
            // 圆心的X坐标
            circleX = (float) getWidth() / 2;
            // 圆心的Y坐标
            circleY = (float) getHeight() / 2 ;
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
            circleRadius = (float) getWidth() / 3;
            // 圆心的X坐标
            circleX = (float) getWidth() / 2;
            // 圆心的Y坐标
            circleY = (float) getHeight() / 2 ;
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

    public boolean getIsRegister() {
        return mIsRegister;
    }

    public boolean isRgbRevert() {
        return rgbRevert;
    }
    public int getMirrorRGB() {
        return mirrorRGB;
    }

}
