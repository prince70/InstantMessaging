package com.niwj.entity;

/**
 * 实体类
 * Created by 伟金 on 2017/7/20.
 */

public class ChatAtName {
    private String nickname;
    private String name;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private int imageId;

    public ChatAtName(String nickname, String name, int imageId) {
        this.nickname = nickname;
        this.name = name;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
