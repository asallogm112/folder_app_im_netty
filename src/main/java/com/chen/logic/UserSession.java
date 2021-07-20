package com.chen.logic;

import com.chen.entity.User;
import com.chen.packet.AbstractPacket;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class UserSession {

	private User user;

	private Channel channel;

	public void sendChannelMsg(AbstractPacket msg) { //ChatMsgPacket
		
		channel.writeAndFlush(msg);
	}

}
