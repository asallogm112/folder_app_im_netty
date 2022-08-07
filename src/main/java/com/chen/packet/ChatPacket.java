package com.chen.packet;

import com.chen.enums.PacketType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class ChatPacket extends AbstractPacket {

	private String sender_id;
	private String sender_name;

	private String receiver_id;
	private String receiver_name;
}
