package com.example.datalibrary.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.baidu.idl.main.facesdk.FaceInfo;
import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.utils.FaceOnDrawTexturViewUtil;

import java.util.List;

/**
 * 用于显示人脸信息的控件
 */
public class BdFaceRectView extends View {
    public static final float LINE_WIDTH = 100f;
    public static final float STROKE_WIDTH = 10f;
    public static final float FACIAL_AREA = 50f;

    private Paint paint;
    private List<LivenessModel> models;
    private List<LivenessModel> lastModels;
    private float identifyThreshold;


    public BdFaceRectView(Context context) {
        this(context, null);
    }

    public BdFaceRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (models == null || models.isEmpty()){
            return;
        }
        for (int i = 0 , k = models.size(); i < k ; i++) {
            LivenessModel model = models.get(i);
            FaceInfo faceInfo = model.getFaceInfo();
            if (lastModels != null && lastModels.size() > i
                    && (lastModels.get(i).getUser() != null ||
                    lastModels.get(i).getScore() > identifyThreshold)){
                paint.setColor(Color.parseColor("#00baf2"));
            }else {
                paint.setColor(Color.parseColor("#FECD33"));
            }
            if (faceInfo == null){
                continue;
            }
            @SuppressLint("DrawAllocation")
            RectF rectF = new RectF();
            rectF.set(FaceOnDrawTexturViewUtil.getFaceRectTwo(faceInfo));
            // 检测图片的坐标和显示的坐标不一样，需要转换。
            FaceOnDrawTexturViewUtil.mapFromOriginalRect(rectF, this, model.getBdFaceImageInstance());
           FaceOnDrawTexturViewUtil.drawFace(getContext() , paint , rectF , canvas , LINE_WIDTH , STROKE_WIDTH , FACIAL_AREA);

        }
    }
    public void drawFaceRect(List<LivenessModel> models , List<LivenessModel> lastModels , float identifyThreshold) {
        this.models = models;
        this.identifyThreshold = identifyThreshold;
        this.lastModels = lastModels;
        postInvalidate();
    }
}