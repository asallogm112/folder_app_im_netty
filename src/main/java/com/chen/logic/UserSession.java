package com.chen.logic;

import com.chen.entity.User;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class UserSession {

	private User user;

	private Channel channel;

	public void sendMsgTo(ChatPacket msg) {
		msg.setMsg_status(AllEnums.MsgStatus_Sent);
		channel.writeAndFlush(msg);
		System.err.println("isActive : " + channel.isActive());
		System.err.println("isOpen : " + channel.isOpen());
		System.err.println("isWritable : " + channel.isWritable());
		System.err.println("isRegistered : " + channel.isRegistered());
		System.err.println("-------------------------\n");
	}

}
