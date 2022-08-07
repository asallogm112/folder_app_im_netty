package com.chen.enums;

public enum MsgStatus {
    MsgStatus_Sending,
    MsgStatus_Sent,     //已发送,存数据库中
    MsgStatus_Failed,   //发送失败
    MsgStatus_Received, //对方已接收,但是未读
    MsgStatus_Read,     //对方已读
}
