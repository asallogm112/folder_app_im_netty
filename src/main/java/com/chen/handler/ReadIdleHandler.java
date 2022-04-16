package com.chen.handler;

import com.chen.logic.SessionManager;
import com.chen.logic.UserSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ReadIdleHandler extends IdleStateHandler {
    
    AttributeKey<UserSession> attributeKey = AttributeKey.valueOf("user_session");
    
    public ReadIdleHandler() {
        super(120, 0, 0);
    }
    
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        
        System.err.println("userEventTriggered" + ctx.channel());
        
        // 心跳包检测读超时
        if (evt instanceof IdleStateEvent) {
            if (((IdleStateEvent) evt).isFirst() == true)
                return;
            
            ctx.channel().close();
            UserSession session = ctx.channel().attr(attributeKey).getAndSet(null);
            if (session != null) {
                ctx.channel().close();
                SessionManager.removeSession(session.getUser().getUser_id());
            }
            ctx.pipeline().remove(this);
    
            InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            System.err.println("IM连接超时了..." + socketAddress.getAddress().getAddress().toString());
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { //todo
//        ctx.channel().close();
//        UserSession session = ctx.channel().attr(attributeKey).getAndSet(null);
//        if (session != null) {
//            ctx.channel().close();
//            SessionManager.removeSession(session.getUser().getUser_id());
//        }
//        ctx.pipeline().remove(this);
        System.err.println("exceptionCaught");
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        UserSession session = ctx.channel().attr(attributeKey).getAndSet(null);
        if (session != null) {
            ctx.channel().close();
            SessionManager.removeSession(session.getUser().getUser_id());
        }
    }
}
