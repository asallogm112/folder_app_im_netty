package com.chen.handler;

import java.nio.charset.Charset;

import com.chen.packet.AbstractPacket;
import com.google.gson.Gson;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<AbstractPacket> {

	@Override
	protected void encode(ChannelHandlerContext ctx, AbstractPacket msg, ByteBuf out) throws Exception {

		out.writeInt(msg.getPacketType());

		String jsonStr = new Gson().toJson(msg);
		byte[] bytes = jsonStr.getBytes(Charset.defaultCharset());
		out.writeInt(bytes.length);
		out.writeBytes(bytes); 
	}

}
