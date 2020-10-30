package com.chen.handler;

import org.springframework.beans.BeanUtils;

import com.chen.entity.OfflineMsg;
import com.chen.logic.SessionManager;
import com.chen.logic.SpringContext;
import com.chen.logic.UserSession;
import com.chen.mapper.OfflineMsgMapper;
import com.chen.packet.AbstractPacket;
import com.chen.packet.AllEnums;
import com.chen.packet.ChatMsgPacket;
import com.chen.packet.PacketType;
import com.chen.packet.ReceiptPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatHandler extends SimpleChannelInboundHandler<AbstractPacket> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AbstractPacket packet) throws Exception {

		if (packet.getPacketType() != PacketType.PacketType_Chat.getPacketType()) {
			ctx.fireChannelRead(packet);
			return;
		}
		System.err.println("聊天消息");
		
		ChatMsgPacket chatPacket = (ChatMsgPacket) packet;

		if (chatPacket.getMsg_type() == AllEnums.MsgType_Image) {
			chatPacket.setWeb_url("https://folder-app.oss-cn-shanghai.aliyuncs.com/" + chatPacket.getWeb_url());
			chatPacket.setThumbnail("https://folder-app.oss-cn-shanghai.aliyuncs.com/" + chatPacket.getThumbnail());
		}
		
		String receiverId = chatPacket.getReceiver_id();
		UserSession session = SessionManager.getSessionBy(receiverId);

		// 插入数据库,防止漏消息 , 对方接受成功后, 发送-回执-去清除数据库
		OfflineMsgMapper offlineMsgMapper = SpringContext.getBean(OfflineMsgMapper.class);
		OfflineMsg record = new OfflineMsg();
		BeanUtils.copyProperties(chatPacket, record);

		ReceiptPacket receipt = new ReceiptPacket();
		BeanUtils.copyProperties(chatPacket, receipt);
		receipt.setMsg_status(AllEnums.MsgStatus_Sent);
		
		try {
			
			offlineMsgMapper.insertSelective(record); //插入离线消息,防止丢消息
			
		} catch (Exception e) {
			e.printStackTrace();
			receipt.setMsg_status(AllEnums.MsgStatus_Failed); // 回执,设置为此消息发送失败
		} 
		
		if (session != null) {
			session.sendChannelMsg(chatPacket);
			receipt.setMsg_status(AllEnums.MsgStatus_Sent); // 插入数据库失败,但是在线 ,回执 设为成功
		} else {
			//todo apns or android push
		} 
		ctx.channel().writeAndFlush(receipt); 
	}

}
