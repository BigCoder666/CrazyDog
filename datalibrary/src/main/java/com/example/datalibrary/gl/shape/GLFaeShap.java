package com.example.datalibrary.gl.shape;

public class GLFaeShap {
    private Shap[] shaps;
    private float length = 0.1f;
    public GLFaeShap(){

        shaps = new Shap[]{new Shap() , new Shap() ,
                new Shap() , new Shap()};

    }
    public void init(int width , int height){
        for (int i = 0; i < shaps.length;i++){
            Shap shap = shaps[i];
            shap.init();
        }
    }
    public void setVertices(float leftTopX ,
                       float leftTopY ,
                       float leftBottomX ,
                       float leftBottomY ,
                       float rightTopX ,
                       float rightTopY ,
                       float rightBottomX ,
                       float rightBottomY){
        shaps[0].setTriangleCoords(new float[]{
                leftTopX  , leftTopY ,
                leftTopX , leftTopY - length ,  // 左下角
                leftTopX , leftTopY , // 右上角
                leftTopX + length, leftTopY, /*0.0f,*/ // 右下角
        });

        shaps[1].setTriangleCoords(new float[]{
                leftBottomX  , leftBottomY ,
                leftBottomX , leftBottomY + length ,  // 左下角
                leftBottomX , leftBottomY , // 右上角
                leftBottomX + length, leftBottomY, /*0.0f,*/ // 右下角
        });
        shaps[2].setTriangleCoords(new float[]{
                rightTopX  , rightTopY ,
                rightTopX , rightTopY - length ,  // 左下角
                rightTopX , rightTopY , // 右上角
                rightTopX - length, rightTopY, /*0.0f,*/ // 右下角
        });
        shaps[3].setTriangleCoords(new float[]{
                rightBottomX  , rightBottomY ,
                rightBottomX , rightBottomY + length ,  // 左下角
                rightBottomX , rightBottomY , // 右上角
                rightBottomX - length, rightBottomY, /*0.0f,*/ // 右下角
        });
    }

    public void setFaceColor(float[] colors){
        for (int i = 0 ; i < shaps.length ; i++){
            shaps[i].setColors(colors);
        }
    }

    public void cleanVertices(){

        shaps[0].setTriangleCoords(new float[]{
                0  , 0 ,
                0 , 0 ,  // 左下角
                0 , 0 , // 右上角
                0 , 0, /*0.0f,*/ // 右下角
        });

        shaps[1].setTriangleCoords(new float[]{
                0  , 0 ,
                0 , 0 ,  // 左下角
                0 , 0 , // 右上角
                0 , 0, /*0.0f,*/ // 右下角
        });
        shaps[2].setTriangleCoords(new float[]{
                0  , 0 ,
                0 , 0 ,  // 左下角
                0 , 0 , // 右上角
                0 , 0, /*0.0f,*/ // 右下角
        });
        shaps[3].setTriangleCoords(new float[]{
                0  , 0 ,
                0 , 0 ,  // 左下角
                0 , 0 , // 右上角
                0 , 0, /*0.0f,*/ // 右下角
        });
    }

    public void onDraw(){
        for (int i = 0; i < shaps.length;i++){
            shaps[i].onDraw();
        }
    }
}
