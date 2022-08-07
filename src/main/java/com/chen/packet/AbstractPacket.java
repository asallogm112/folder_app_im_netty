package com.chen.packet;

import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.nio.charset.Charset;

@Data
public abstract class AbstractPacket {

	private int msg_id; //4 byte
	private int msg_type;
	private int msg_status;
	private int packet_type;
	private int create_timestamp; //4 byte
	private String token;
	
}
