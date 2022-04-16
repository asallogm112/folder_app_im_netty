package com.chen.handler;

import java.nio.charset.Charset;

import com.chen.packet.AbstractPacket;
import com.google.gson.Gson;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class PacketEncoder extends MessageToByteEncoder<AbstractPacket> {

	@Override
	protected void encode(ChannelHandlerContext ctx, AbstractPacket pkt, ByteBuf out) throws Exception {
		
		System.err.println("PacketEncoder");
		
		out.writeInt(pkt.getPacketType()); //packet type

		String jsonStr = new Gson().toJson(pkt);
		
		byte[] bytes = jsonStr.getBytes(Charset.forName("UTF-8"));
		
		out.writeInt(bytes.length);
		out.writeBytes(bytes); 
		
		System.err.println("----- PacketEncoder bytes len : ------- " + bytes.length);
	}

}
