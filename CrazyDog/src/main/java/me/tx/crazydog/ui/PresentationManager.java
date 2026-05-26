package me.tx.crazydog.ui;

import com.alibaba.fastjson.JSONObject;

import java.util.Stack;

public class PresentationManager {
    private static volatile PresentationManager instance;
    private final Stack<CrazyPresentation> stack = new Stack<>();

    private PresentationManager() {}

    public static PresentationManager getInstance() {
        if (instance == null) {
            synchronized (PresentationManager.class) {
                if (instance == null) {
                    instance = new PresentationManager();
                }
            }
        }
        return instance;
    }

    // 入栈
    public void addPresentation(CrazyPresentation presentation) {
        stack.push(presentation);
    }

    // 出栈
    public void removePresentation(CrazyPresentation presentation) {
        if (stack.contains(presentation)) {
            stack.remove(presentation);
        }
    }

    // 跳转新页面
    public void startPresentation(CrazyPresentation presentation) {
        if (!presentation.isShowing()) {
            presentation.show();
        }
    }

    // 关闭页面
    public void finishPresentation(CrazyPresentation presentation) {
        if (presentation.isShowing()) {
            presentation.dismiss();
        }
    }

    // 关闭全部
    public void finishAll() {
        while (!stack.isEmpty()) {
            CrazyPresentation p = stack.pop();
            if (p.isShowing()) p.dismiss();
        }
    }

    // 获取栈顶（最上层）
    public CrazyPresentation getTopPresentation() {
        if (!stack.isEmpty()) {
            return stack.peek();
        }
        return null;
    }

    // 分发消息 → 只给栈顶
    public void dispatchMessage(String type, JSONObject data) {
        CrazyPresentation top = getTopPresentation();
        if (top != null) {
            top.onActivityMessage(type, data);
        }
    }
}