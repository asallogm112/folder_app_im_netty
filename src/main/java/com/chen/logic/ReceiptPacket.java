package com.chen.logic;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ReceiptPacket extends AbstractPacket {

	private String sender_id;
	private String receiver_id;

	@Override
	public int getPacketType() {
		return PacketType.PacketType_Receipt.getPacketType();
	}

}
