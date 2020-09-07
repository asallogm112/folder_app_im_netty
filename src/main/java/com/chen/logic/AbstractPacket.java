package com.chen.logic;

import lombok.Data;

@Data
public abstract class AbstractPacket {

	private int msg_id;
	private int msg_type;
	private int msg_status;
	private String token;

	public abstract int getPacketType();

}
