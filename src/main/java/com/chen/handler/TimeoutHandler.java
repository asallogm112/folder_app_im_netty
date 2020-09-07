package com.chen.handler;

import com.chen.logic.SessionManager;
import com.chen.logic.UserSession;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

public class TimeoutHandler extends ChannelInboundHandlerAdapter {

	AttributeKey<UserSession> attributeKey = AttributeKey.valueOf("user_session");

	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

		System.err.println("userEventTriggered" + ctx.channel());

		// 心跳包检测读超时
		if (evt instanceof IdleStateEvent) {
			ctx.channel().close();
			UserSession session = ctx.channel().attr(attributeKey).getAndSet(null);
			SessionManager.removeSession(session.getUser().getUser_id());
			ctx.pipeline().remove(this);
			System.err.println("IM连接超时了...");
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		UserSession session = ctx.channel().attr(attributeKey).getAndSet(null);
		if (session != null) {
			SessionManager.removeSession(session.getUser().getUser_id());
			System.err.println("channelInactive : " + session.getUser().getUser_id());
		}
	}
}
