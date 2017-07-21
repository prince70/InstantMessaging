/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.niwj.entity;

/**
 * 聊天消息javabean
 *还缺少语音
 * @author
 */
public class Message {
    public final static int MSG_TYPE_TEXT = 3;  //文本
    public final static int MSG_TYPE_PHOTO = 1; //图片
    public final static int MSG_TYPE_FACE = 2;  //表情
    public final static int MSG_TYPE_VOICE = 4;  //语音

    public final static int MSG_STATE_SENDING = 3;
    public final static int MSG_STATE_SUCCESS = 1;
    public final static int MSG_STATE_FAIL = 2;

    private Long id;
    private int type; // 0-text | 1-photo | 2-face | more type ...
    private int state; // 0-sending | 1-success | 2-fail
    private String fromUserName;      //是谁发送的  名字
    private String fromUserAvatar;  //是谁发送的 头像
    private String toUserName;        //发给谁  名字
    private String toUserAvatar;     //发给谁 头像
    private String content;   //内容

    private Boolean isSend;   //是否为发送方
    private Boolean sendSucces;      //是否发送成功
    private String time;      //时间

    public Message(int type, int state, String fromUserName,
                   String fromUserAvatar, String toUserName, String toUserAvatar,
                   String content, Boolean isSend, Boolean sendSucces, String time) {
        super();
        this.type = type;
        this.state = state;
        this.fromUserName = fromUserName;
        this.fromUserAvatar = fromUserAvatar;
        this.toUserName = toUserName;
        this.toUserAvatar = toUserAvatar;
        this.content = content;
        this.isSend = isSend;
        this.sendSucces = sendSucces;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Boolean getIsSend() {
        return isSend;
    }

    public void setIsSend(Boolean isSend) {
        this.isSend = isSend;
    }

    public Boolean getSendSucces() {
        return sendSucces;
    }

    public void setSendSucces(Boolean sendSucces) {
        this.sendSucces = sendSucces;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
