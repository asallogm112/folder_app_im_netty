package com.chen.handler;

import com.chen.logic.SpringContext;
import com.chen.mapper.extend.OfflineMsgMapperExtend;
import com.chen.packet.AbstractPacket;
import com.chen.packet.PacketType;
import com.chen.packet.ReceiptPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ReceiptHandler extends SimpleChannelInboundHandler<AbstractPacket> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AbstractPacket packet) throws Exception {

		if (packet.getPacketType() != PacketType.PacketType_Receipt.getPacketType()) {
			ctx.fireChannelRead(packet);
			return;
		} 
		System.err.println("回执包");
		ReceiptPacket receipt = (ReceiptPacket) packet;
		OfflineMsgMapperExtend offlineMsgMapper = SpringContext.getBean(OfflineMsgMapperExtend.class);
		offlineMsgMapper.deleteOfflineMsg(receipt);
	}

}
