package com.chen.packet;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ChatMsgPacket extends AbstractPacket {

	private String sender_id;
	private String sender_name;
	private String sender_avatar;

	private String receiver_id;
	private String receiver_name;
	private String receiver_avatar;

	private String text;
	private String sub_text; // 额外的

	private double latitude;
	private double longitude;

	private int image_width;
	private int image_height;

	private String image_url; // 网络图片URL

	private int duration; // MsgType_Video && MsgType_Audio
	private String audio_url; // MsgType_Video && MsgType_Audio
	private String video_url; // MsgType_Video && MsgType_Audio

	@Override
	public int getPacketType() {
		return PacketType.PacketType_Chat.getPacketType();
	}
}
