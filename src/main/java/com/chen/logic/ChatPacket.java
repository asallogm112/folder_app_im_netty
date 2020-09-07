package com.chen.logic;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ChatPacket extends AbstractPacket {

	private String sender_id;
	private String receiver_id;

	private String text;
	private String extra;

	private String web_url;

	private double latitude;
	private double longitude;

	private String sender_name;
	private String receiver_name;

	private String sender_avatar;
	private String receiver_avatar;

	private int duration;
	private String thumbnail;

	private int create_time;

	private int image_width;
	private int image_height;

	@Override
	public int getPacketType() {
		return PacketType.PacketType_Chat.getPacketType();
	}
}
