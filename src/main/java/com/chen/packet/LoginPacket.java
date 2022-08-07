package com.chen.packet;

import com.chen.enums.PacketType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LoginPacket extends AbstractPacket {

	private String user_id;
	
}
