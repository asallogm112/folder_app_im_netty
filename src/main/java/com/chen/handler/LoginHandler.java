package com.chen.handler;

import java.util.List;

import org.springframework.beans.BeanUtils;

import com.chen.entity.OfflineMsg;
import com.chen.entity.User;
import com.chen.logic.AbstractPacket;
import com.chen.logic.AsyncTask;
import com.chen.logic.ChatPacket;
import com.chen.logic.LoginPacket;
import com.chen.logic.PacketType;
import com.chen.logic.SessionManager;
import com.chen.logic.SpringContext;
import com.chen.logic.UserSession;
import com.chen.mapper.OfflineMsgMapper;

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

		UserSession isExisted = SessionManager.getSessionBy(login.getUser_id());

		OfflineMsgMapper offlineMsgMapper = SpringContext.getBean(OfflineMsgMapper.class);
		List<OfflineMsg> offlineMsgs = offlineMsgMapper.selectByReceiverId(login.getUser_id());

		if (offlineMsgs != null && offlineMsgs.size() > 0) {
			AsyncTask task = SpringContext.getBean(AsyncTask.class);
			task.addTask(new Runnable() {

				@Override
				public void run() {
					for (OfflineMsg offlineMsg : offlineMsgs) {
						ChatPacket chat = new ChatPacket();
						BeanUtils.copyProperties(offlineMsg, chat);
						session.sendMsgTo(chat);
					}
				}
			});
		}

		if (isExisted == null) {

			SessionManager.registerSession(session);
			ctx.pipeline().remove(this);

		} else {
			SessionManager.removeSession(login.getUser_id());
			SessionManager.registerSession(session);
		}
	}

}
