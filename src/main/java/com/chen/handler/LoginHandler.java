package com.chen.handler;

import java.util.List;

import org.springframework.beans.BeanUtils;

import com.chen.entity.OfflineMsg;
import com.chen.entity.User;
import com.chen.logic.AsyncTask;
import com.chen.logic.SessionManager;
import com.chen.logic.SpringContext;
import com.chen.logic.UserSession;
import com.chen.mapper.extend.MsgReceiptMapperExtend;
import com.chen.mapper.extend.OfflineMsgMapperExtend;
import com.chen.packet.AbstractPacket;
import com.chen.packet.ChatMsgPacket;
import com.chen.packet.LoginPacket;
import com.chen.packet.PacketType;
import com.chen.packet.ReceiptPacket;

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

		OfflineMsgMapperExtend offlineMsgMapper = SpringContext.getBean(OfflineMsgMapperExtend.class);
		List<OfflineMsg> offlineMsg_list = offlineMsgMapper.selectOfflineListByReceiver_id(login.getUser_id());

		MsgReceiptMapperExtend receiptMapper = SpringContext.getBean(MsgReceiptMapperExtend.class);

		List<ReceiptPacket> receipt_list = receiptMapper.selectReceiptListBySender_id(login.getUser_id());

		// 对方上线之后 , 给他发送离线消息 (异步)
		if (offlineMsg_list != null && offlineMsg_list.size() > 0) {

			System.err.println("offlineMsgs : " + offlineMsg_list.size());

			AsyncTask task = SpringContext.getBean(AsyncTask.class);
			task.addTask(new Runnable() {

				@Override
				public void run() {
					for (OfflineMsg offlineMsg : offlineMsg_list) {

						ChatMsgPacket chat = new ChatMsgPacket();
						BeanUtils.copyProperties(offlineMsg, chat);
						session.sendChannelMsg(chat);
					}
				}
			});
		}

		// 对方上线之后 , 给他发送离线 回执 (异步)
		if (receipt_list != null && receipt_list.size() > 0) {

			receiptMapper.deleteReceiptListBy_id(user.getUser_id());
			System.err.println("offlineMsgs : " + offlineMsg_list.size());

			AsyncTask task = SpringContext.getBean(AsyncTask.class);
			task.addTask(new Runnable() {

				@Override
				public void run() {
					for (ReceiptPacket receipt : receipt_list) {
						session.sendChannelMsg(receipt);
					}
				}
			});
		}

		UserSession current_user_session = SessionManager.getSessionBy(login.getUser_id());
		if (current_user_session == null) {

			SessionManager.registerSession(session);
			ctx.pipeline().remove(this);
		} else {

			SessionManager.removeSession(login.getUser_id()); // 把之前的 顶掉 (也有可能是脏数据)
			SessionManager.registerSession(session);
		}
	}

}
