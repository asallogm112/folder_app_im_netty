package com.chen.packet;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ChatMsgPacket extends AbstractPacket {

	private String sender_id;
	private String receiver_id;

	private String text;
	private String extra; //额外的

	private String web_url;

	private double latitude;
	private double longitude;

	private String sender_name;
	private String receiver_name;

	private String sender_avatar;
	private String receiver_avatar;

	private String thumbnail;

	private int duration;
	private int create_time;

	private int image_width;
	private int image_height;

	@Override
	public int getPacketType() {
		return PacketType.PacketType_Chat.getPacketType();
	}
}
