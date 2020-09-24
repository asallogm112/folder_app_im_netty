package com.chen.logic;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LoginPacket extends AbstractPacket {

	private String user_id;
	private String phone;
	private String password;
	private String avatar_small;
	private String avatar_large;
	private String nick_name;
	private String open_id;
	private String alipay_id;
	private String gender;
	  
	@Override
	public int getPacketType() {
		return PacketType.PacketType_Login.getPacketType();
	}

}
