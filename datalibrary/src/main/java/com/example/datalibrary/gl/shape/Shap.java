package com.example.datalibrary.gl.shape;

import android.graphics.Rect;
import android.opengl.GLES20;

import com.example.datalibrary.gl.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Shap {

    public void setTriangleCoords(float[] triangleCoords) {
        this.vertices = triangleCoords;
        verFloatBuffer = getVertices();
    }
    public void setTriangleCoords(float x , float y) {
        this.vertices = new float[]{
                x  /*左上角 x*/, y /*左上角 y*/, /*0.0f,*/
                x /*左下角 x*/, y - 0.15f /*左下角 y*/, /*0.0f,*/ // 左下角
                x /*右上角 x*/, y /*右上角 y*/, /*0.0f, */// 右上角
                x + 0.15f, y, /*0.0f,*/ // 右下角
        };
        verFloatBuffer = getVertices();
    }
    // 三维的顶点坐标，有方向的
    private float[] vertices = new float[]{
            -0f  /*左上角 x*/ , 0f /*左上角 y*/, /*0.0f,*/
            -0f /*左下角 x*/, -0f /*左下角 y*/, /*0.0f,*/ // 左下角
        -0f  /*左上角 x*/ , 0f /*左上角 y*/, /*0.0f,*/
        0f  /*左上角 x*/ , 0f /*左上角 y*/, /*0.0f,*/
    };

    private static final short[] INDICES =new short[] {
            0 , 1 , 2 , 3
    };

    public void setColors(float[] mColors) {
        this.mColors = mColors;
    }

    // 颜色
    private float[] mColors = new float[]{0f , 0f , 0f , 0f};

    /* 顶点着色器的脚本 */
    private static final String VERTEX_SHADER_CODE
            = "attribute vec2 vPosition;            " // 顶点位置属性vPosition
            + "void main(){                         "
            + "   gl_Position = vec4(vPosition,0,1);" // 确定顶点位置
            + "}";

    /* 片元着色器的脚本 */
    private static final String FRAGMENT_SHADER_CODE
            = "precision mediump float;         " // 声明float类型的精度为中等(精度越高越耗资源)
            + "uniform vec4 uColor;             " // uniform的属性uColor
            + "void main(){                     "
            + "   gl_FragColor = uColor;        " // 给此片元的填充色
            + "}";
    private FloatBuffer vertexBuffer;
    private ShortBuffer indiceBuffer;
    private int mPositionHandle; //顶点
    private int mColorHandle; // 颜色
    // 顶点buffer
    private int mProgram; //
    private int iboId; // IBO的ID
    public Shap(){
        // 1、存储顶点坐标
        vertexBuffer = ByteBuffer.allocateDirect(4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();;
        vertexBuffer.position(0);
    }
    private Rect rect = new Rect();
    private int width;
    private int height;
    private int screenWidth;
    private int screenHeight;
    // 获取图形的顶点坐标
    FloatBuffer verFloatBuffer;

    public void setSize(int screenWidth,int screenHeight,int videoWidth,int videoHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.width = videoWidth;
        this.height = videoHeight;
        rect();
    }
    private void rect(){
        int left;
        int top;
        int viewWidth;
        int viewHeight;
        float sh = screenWidth*1.0f/screenHeight;
        float vh = width *1.0f/ height;
        if (sh < vh){
            left = 0;
            viewWidth = screenWidth;
            viewHeight = (int)(height *1.0f/ width *viewWidth);
            top = (screenHeight - viewHeight)/2;
        }else{
            top = 0;
            viewHeight = screenHeight;
            viewWidth = (int)(width *1.0f/ height *viewHeight);
            left = (screenWidth - viewWidth)/2;
        }
        rect.left = left;
        rect.top = top;
        rect.right = viewWidth;
        rect.bottom = viewHeight;
    }

    public void init(){
        ByteBuffer mbb = ByteBuffer.allocateDirect(INDICES.length * 2);
        // 数组排列用nativeOrder
        mbb.order(ByteOrder.nativeOrder());
        indiceBuffer = mbb.asShortBuffer();
        indiceBuffer.put(INDICES);
        indiceBuffer.flip();
        mProgram = ShaderUtils.createProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);

        int[] ibos = new int[1];
        GLES20.glGenBuffers(ibos.length, ibos, 0);

        this.iboId =  ibos[0];
        // 绑定VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, iboId);

        //赋值
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,INDICES.length * 2,indiceBuffer,GLES20.GL_STATIC_DRAW);

        //解绑
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        verFloatBuffer = getVertices();
    }

    public void onDraw(){

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        // 使用某套shader程序
        GLES20.glUseProgram(mProgram);
        // 为画笔指定顶点位置数据(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, verFloatBuffer);
        // 允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // 设置属性uColor(颜色 索引,R,G,B,A)
        GLES20.glUniform4f(mColorHandle, mColors[0] / 255f, mColors[1] / 255f, mColors[2] / 255f, mColors[3] / 255f);
        // 绘制
//        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 4,GLES20.GL_UNSIGNED_SHORT,0);
        GLES20.glLineWidth(10);
        GLES20.glDrawArrays(GLES20.GL_LINES , 0, 4);
    }
    /**
     * 获取图形的顶点
     * 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
     * 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
     *
     * @return 顶点Buffer
     */
    private FloatBuffer getVertices() {
        // 创建顶点坐标数据缓冲
        // vertices.length*4是因为一个float占四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());             //设置字节顺序
        FloatBuffer vertexBuf = vbb.asFloatBuffer();    //转换为Float型缓冲
        vertexBuf.put(vertices);                        //向缓冲区中放入顶点坐标数据
        vertexBuf.position(0);                          //设置缓冲区起始位置

        return vertexBuf;
    }

    /**
     *  初始化buffer
     *  */
    private static FloatBuffer initBuffer(float[] buffers,int len) {
        // 先初始化buffer,数组的长度*4,因为一个float占4个字节
        ByteBuffer mbb = ByteBuffer.allocateDirect(buffers.length * len);
        // 数组排列用nativeOrder
        mbb.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = mbb.asFloatBuffer();
        floatBuffer.put(buffers);
        floatBuffer.flip();
        return floatBuffer;
    }
    /**
     * 加载shader
     * @param type 片元、顶点
     * @param shaderCode Code
     * @return int
     */
    private int loadShader(int type, String shaderCode) {
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }


}
