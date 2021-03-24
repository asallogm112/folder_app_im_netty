package com.chen.handler;

import com.chen.logic.SessionManager;
import com.chen.logic.SpringContext;
import com.chen.logic.UserSession;
import com.chen.mapper.extend.OfflineMsgMapperExtend;
import com.chen.packet.AbstractPacket;
import com.chen.packet.AllEnums;
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
		offlineMsgMapper.deleteOfflineMsg(receipt); //删除数据库 - 对应的msg
		
		receipt.setMsg_status(AllEnums.MsgStatus_Received); //告诉发送者 , 对方已经接收 (received) or 已读 (read)
		
		String sender_id = receipt.getSender_id();
		
		UserSession sender_session = SessionManager.getSessionBy(sender_id);
		
		if (sender_session != null) {
			 
			sender_session.sendChannelMsg(receipt);
		}
	}

}
