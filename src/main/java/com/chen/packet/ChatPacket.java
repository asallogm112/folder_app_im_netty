package com.chen.packet;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ChatPacket extends AbstractPacket {

	private String sender_id;
	private String sender_name;
	private String sender_avatar;

	private String receiver_id;
	private String receiver_name;
	private String receiver_avatar;
	
	private int msg_type;
	private int msg_status;
	
	private String text;
	private String sub_text; // 额外的

	private Double latitude;
	private Double longitude;

	private Integer image_width;
	private Integer image_height;

	private String image_url; // 网络图片URL

	private Integer duration; // MsgType_Video && MsgType_Audio
	private String audio_url; // MsgType_Video && MsgType_Audio
	private String video_url; // MsgType_Video && MsgType_Audio

	@Override
	public int getPacketType() {
		return PacketType.PacketType_Chat.getPacketType();
	}
}
