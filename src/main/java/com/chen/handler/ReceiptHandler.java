package com.chen.handler;

import com.chen.logic.SessionManager;
import com.chen.logic.SpringContext;
import com.chen.logic.UserSession;
import com.chen.mapper.extend.MsgReceiptMapperExtend;
import com.chen.mapper.extend.OfflineMsgMapperExtend;
import com.chen.packet.ReceiptPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class ReceiptHandler extends SimpleChannelInboundHandler<ReceiptPacket> {
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ReceiptPacket packet) throws Exception {
        
        System.err.println("回执包 : " + packet);
        
        ReceiptPacket receipt = (ReceiptPacket) packet;
        
        OfflineMsgMapperExtend offlineMsgMapper = SpringContext.getBean(OfflineMsgMapperExtend.class);
        
        offlineMsgMapper.deleteOfflineMsg(receipt); // 删除数据库 -> msg status = read
        
        String sender_id = receipt.getSender_id();
        
        UserSession sender_session = SessionManager.getSessionBy(sender_id);
        
        if (sender_session != null) { //此消息 发送者 online
            
            sender_session.sendChannelMsg(receipt);
        } else { // 发送人 不在线, 将receipt 存库
            MsgReceiptMapperExtend receiptMapper = SpringContext.getBean(MsgReceiptMapperExtend.class);
            receiptMapper.insertSelective(receipt);
        }
        
        ctx.channel().writeAndFlush(receipt);
    }
    
}
