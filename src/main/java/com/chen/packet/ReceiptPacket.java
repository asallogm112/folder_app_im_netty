package com.chen.packet;

import com.chen.enums.PacketType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ReceiptPacket extends AbstractPacket {

	private String sender_id;
	private String receiver_id;
	
	@Override
	public int getPacket_type() {
		return PacketType.PacketType_Receipt.ordinal();
	}
	
	@Override
	public void setPacket_type(int packet_type) {
		super.setPacket_type(PacketType.PacketType_Receipt.ordinal());
	}
}
