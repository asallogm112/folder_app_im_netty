package com.chen.handler;

import com.chen.logic.AbstractPacket;
import com.chen.logic.PacketType;
import com.chen.logic.ReceiptPacket;
import com.chen.logic.SpringContext;
import com.chen.mapper.OfflineMsgMapper;

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
		OfflineMsgMapper offlineMsgMapper = SpringContext.getBean(OfflineMsgMapper.class);
		offlineMsgMapper.deleteOfflineMsg(receipt);
	}

}
