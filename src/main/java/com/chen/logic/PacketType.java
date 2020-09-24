package com.chen.logic;

import java.util.HashMap;
import java.util.Map;

public enum PacketType {

	PacketType_HeartBeat(0, null),
	PacketType_Login    (1, LoginPacket.class),
	PacketType_Chat     (2, ChatMsgPacket.class),
	PacketType_Receipt  (3, ReceiptPacket.class);

	private int packetType;
	private Class<? extends AbstractPacket> packetClass;

	private static Map<Integer, Class<? extends AbstractPacket>> packetClasses = new HashMap<>();

	private PacketType(int packType, Class<? extends AbstractPacket> packetClass) {
		this.packetType = packType;
		this.packetClass = packetClass;
	}

	public int getPacketType() {
		return packetType;
	}

	public void setPacketType(int packetType) {
		this.packetType = packetType;
	}

	public Class<? extends AbstractPacket> getPacketClass() {
		return packetClass;
	}

	public void setPacketClass(Class<? extends AbstractPacket> packetClass) {
		this.packetClass = packetClass;
	}

	public static void initPackets() {

		for (PacketType packet : PacketType.values()) {
			int type = packet.getPacketType(); 
			Class<? extends AbstractPacket> packetClass = packet.getPacketClass();
			packetClasses.put(type, packetClass); 
		}
	}

	public static Class<? extends AbstractPacket> getPacketClass(int packetType) {

		return packetClasses.get(packetType);
	}
}
