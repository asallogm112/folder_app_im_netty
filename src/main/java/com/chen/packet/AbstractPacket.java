package com.chen.packet;

import lombok.Data;

@Data
public abstract class AbstractPacket {

	private int msg_id;
	private int create_timestamp;
	private String token;

	public abstract int getPacketType();
}
