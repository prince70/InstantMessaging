package com.niwj.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by 伟金 on 2017/7/18.
 * 聊天记录
 */

public class SessionMessage extends DataSupport {
    private int id;//自增长
    private int type;
    private int state;
    private String fromUserName;
    private String fromUserAvatar;
    private String toUserName;
    private String toUserAvatar;
    private String content;
    private boolean isSend;
    private boolean sendSuccess;
    private String time;  //String 方式好做比较

    @Override
    public String toString() {
        return "SessionMessage{" +
                "id=" + id +
                ", type=" + type +
                ", state=" + state +
                ", fromUserName='" + fromUserName + '\'' +
                ", fromUserAvatar='" + fromUserAvatar + '\'' +
                ", toUserName='" + toUserName + '\'' +
                ", toUserAvatar='" + toUserAvatar + '\'' +
                ", content='" + content + '\'' +
                ", isSend=" + isSend +
                ", sendSuccess=" + sendSuccess +
                ", time='" + time + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromUserAvatar() {
        return fromUserAvatar;
    }

    public void setFromUserAvatar(String fromUserAvatar) {
        this.fromUserAvatar = fromUserAvatar;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getToUserAvatar() {
        return toUserAvatar;
    }

    public void setToUserAvatar(String toUserAvatar) {
        this.toUserAvatar = toUserAvatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public boolean isSendSuccess() {
        return sendSuccess;
    }

    public void setSendSuccess(boolean sendSuccess) {
        this.sendSuccess = sendSuccess;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
