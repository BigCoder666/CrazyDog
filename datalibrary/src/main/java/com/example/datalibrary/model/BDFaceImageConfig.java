package com.example.datalibrary.model;

import android.graphics.Bitmap;

import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;

public class BDFaceImageConfig {
    public BDFaceSDKCommon.BDFaceImageType bdFaceImageType;
    public byte[] data; // 可见光YUV 数据流
    public int srcHeight; // 可见光YUV 数据流-高度
    public int srcWidth; // 可见光YUV 数据流-宽度
    public int direction ; // rgb角度
    public int mirror; // rgb镜像
    public Bitmap bitmap; // 图片对象
    public BDFaceImageConfig(int srcHeight , int srcWidth ,
                             int direction , int mirror ,
                             BDFaceSDKCommon.BDFaceImageType bdFaceImageType){
        this.srcHeight = srcHeight;
        this.srcWidth = srcWidth;
        this.direction = direction;
        this.mirror = mirror;
        this.bdFaceImageType = bdFaceImageType;
    }
    public BDFaceImageConfig(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
