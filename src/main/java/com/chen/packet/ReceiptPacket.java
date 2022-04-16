package com.chen.packet;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ReceiptPacket extends AbstractPacket {

	private String sender_id;
	private String receiver_id;
	private int msg_status;
	
	@Override
	public int getPacketType() {
		return PacketType.PacketType_Receipt.getPacketType();
	}

}
