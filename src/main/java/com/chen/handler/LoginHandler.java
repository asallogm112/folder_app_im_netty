package com.chen.handler;

import java.util.List;

import org.springframework.beans.BeanUtils;

import com.chen.entity.OfflineMsg;
import com.chen.entity.User;
import com.chen.logic.AsyncTask;
import com.chen.logic.SessionManager;
import com.chen.logic.SpringContext;
import com.chen.logic.UserSession;
import com.chen.mapper.extend.OfflineMsgMapperExtend;
import com.chen.packet.AbstractPacket;
import com.chen.packet.ChatMsgPacket;
import com.chen.packet.LoginPacket;
import com.chen.packet.PacketType;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LoginHandler extends SimpleChannelInboundHandler<AbstractPacket> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AbstractPacket packet) throws Exception {

		if (packet.getPacketType() != PacketType.PacketType_Login.getPacketType()) {
			ctx.fireChannelRead(packet);
			return;
		}

		System.err.println("登录包");
		LoginPacket login = (LoginPacket) packet;

		User user = new User();
		BeanUtils.copyProperties(login, user);

		UserSession session = new UserSession();
		session.setChannel(ctx.channel());
		session.setUser(user);

		UserSession existSession = SessionManager.getSessionBy(login.getUser_id());
		
		OfflineMsgMapperExtend offlineMsgMapper = SpringContext.getBean(OfflineMsgMapperExtend.class);
		List<OfflineMsg> offlineMsgs = offlineMsgMapper.selectByReceiverId(login.getUser_id());

		//对方上线之后 , 给他发送离线消息 (异步)
		if (offlineMsgs != null && offlineMsgs.size() > 0) {
			
			AsyncTask task = SpringContext.getBean(AsyncTask.class);
			task.addTask(new Runnable() {

				@Override
				public void run() {
					for (OfflineMsg offlineMsg : offlineMsgs) {
						ChatMsgPacket chat = new ChatMsgPacket();
						BeanUtils.copyProperties(offlineMsg, chat);
						session.sendChannelMsg(chat);
					}
				}
			});
		}

		if (existSession == null) {
			SessionManager.registerSession(session);
			ctx.pipeline().remove(this);
		} else {
			SessionManager.removeSession(login.getUser_id());
			SessionManager.registerSession(session);
		}
	}

}
