package com.chen.packet;

public class AllEnums {

	//msg_status
	public static final int MsgStatus_Sending = 0; //发送中...
	public static final int MsgStatus_Sent = 1; //已发送,存数据库中
	public static final int MsgStatus_Failed = 2; //发送失败
	public static final int MsgStatus_Received = 3; //对方已接收,但是未读
	public static final int MsgStatus_Read = 4; //对方已读

	//msg_type
	public static final int MsgType_Text = 0;
	public static final int MsgType_Image = 1;
	public static final int MsgType_Audio = 2;
	public static final int MsgType_Video = 3;
	
	public static final int MsgType_Location = 4;
	public static final int MsgType_Product = 5;
	
	public static final int MsgType_MiniProgram = 5;
	public static final int MsgType_Subscribe = 6;
}
