package com.example.datalibrary.manager;

import com.example.datalibrary.model.User;

/**
 * 人脸识别结果
 */
public class IdentifyResult {
    public User user;  // 识别的用户信息
    public int index; // 识别的用户索引
    public float score; // 识别的分值
    public IdentifyResult() {
        this.user = new User();
        this.index = -1;
        this.score = -1;
    }

    public IdentifyResult(User user, int index, float score) {
        this.user = user;
        this.index = index;
        this.score = score;
    }
}
