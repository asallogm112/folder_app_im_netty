package com.chen.logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum PacketType {

	PacketType_HeartBeat(0, null), PacketType_Login(1, LoginPacket.class), PacketType_Chat(2, ChatPacket.class),
	PacketType_Receipt(3, ReceiptPacket.class);

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
		Set<Integer> typeSet = new HashSet<>();
		Set<Class<?>> packets = new HashSet<>();

		for (PacketType p : PacketType.values()) {
			int type = p.getPacketType();
			if (typeSet.contains(type)) {
				throw new IllegalStateException("packet type 协议类型重复" + type);
			}
			Class<?> packet = p.getPacketClass();
			packetClasses.put(type, p.getPacketClass());
			typeSet.add(type);
			packets.add(packet);
		}
	}

	public static Class<? extends AbstractPacket> getPacketClass(int packetType) {

		return packetClasses.get(packetType);
	}
}
