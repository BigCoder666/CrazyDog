package com.example.datalibrary.deptrum;

public class GLDisplay {
    private boolean mIsDisplayStarted;
    private GLFrameRenderer render;

    /**
     * 预览渲染
     *
     * @param view               需要渲染的视图
     * @param displayOrientation 预览方向
     * @param displayMirror      预览镜像
     * @param displayOrientation 预览方向
     * @param data               预览的图像数据
     * @param renderWidth        预览宽
     * @param renderHeight       预览高
     * @param dataType           图像数据类型 0:yuv420p 1:rgb24
     * @return void
     */
    public synchronized void render(
            GLFrameSurface view,
            int displayOrientation,
            boolean displayMirror,
            byte[] data,
            int renderWidth,
            int renderHeight,
            int dataType) {
        render = view.getCurrentRenderer();
        render.setResolution(renderWidth, renderHeight);
        if (view != null && render != null) {
            if (!mIsDisplayStarted) {
                render.update(renderWidth, renderHeight, dataType);
                view.onResume();
                mIsDisplayStarted = true;
            }
            render.setDisplayOrientation(displayOrientation);
            render.displayMirror(displayMirror);
            render.update(data, dataType);
        }
    }

    /**
     * 释放预览
     *
     * @return void
     */
    public void release() {
        if (render != null) {
            render.release();
            this.mIsDisplayStarted = false;
        }
    }
}
