package com.chen.handler;

import com.chen.entity.OfflineMsg;
import com.chen.logic.SessionManager;
import com.chen.logic.SpringContext;
import com.chen.logic.UserSession;
import com.chen.mapper.OfflineMsgMapper;
import com.chen.packet.AllEnums;
import com.chen.packet.ChatPacket;
import com.chen.packet.ReceiptPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeanUtils;

@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<ChatPacket> {
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatPacket packet) throws Exception {
        
        ChatPacket pkt = (ChatPacket) packet;
        pkt.setMsg_status(AllEnums.MsgStatus_Sent);
        
        System.err.println("聊天消息 text: " + pkt.getText());
        
        if (pkt.getMsg_type() == AllEnums.MsgType_Image) {
            pkt.setImage_url("https://folder-app.oss-cn-shanghai.aliyuncs.com/" + pkt.getImage_url() + "/large");
        } else if (pkt.getMsg_type() == AllEnums.MsgType_Video) {
            pkt.setImage_url("https://folder-app.oss-cn-shanghai.aliyuncs.com/" + pkt.getImage_url() + "/large");
            pkt.setVideo_url("https://folder-app.oss-cn-shanghai.aliyuncs.com/" + pkt.getVideo_url());
        } else if (pkt.getMsg_type() == AllEnums.MsgType_Audio) {
            pkt.setAudio_url("https://folder-app.oss-cn-shanghai.aliyuncs.com/" + pkt.getAudio_url());
        }
        
        String receiverId = pkt.getReceiver_id();
        UserSession receiver_session = SessionManager.getSessionBy(receiverId);
    
        if (receiverId.length() > 0){
            return;
        }
        // 插入数据库,防止漏消息 , 对方接受成功后, 发送-回执-去清除数据库
        OfflineMsgMapper offlineMsgMapper = SpringContext.getBean(OfflineMsgMapper.class);
        
        OfflineMsg record = new OfflineMsg();
        BeanUtils.copyProperties(pkt, record);
        
        //receipt
        ReceiptPacket receipt = new ReceiptPacket();
        BeanUtils.copyProperties(pkt, receipt);
        receipt.setMsg_status(AllEnums.MsgStatus_Sent);
        
        try {
            
            offlineMsgMapper.insertSelective(record); // 不管是否在线, 插入离线消息,防止丢消息
            
        } catch (Exception e) {
            e.printStackTrace();
            receipt.setMsg_status(AllEnums.MsgStatus_Failed); // 回执,设置为此消息发送失败
        }
        
        if (receiver_session != null) {
            
            receipt.setMsg_status(AllEnums.MsgStatus_Received); // 可能插入数据库失败,但是在线 =>回执 设为成功
            
            pkt.setMsg_status(AllEnums.MsgStatus_Received);
            receiver_session.sendChannelMsg(pkt);
            
        } else {
            // TODO apns or android push
        }
        //一定要放在这, 如果接受者online, 直接发送 received 回执
        ctx.channel().writeAndFlush(receipt); //给自己回执(已发送),否则一直在转圈圈
    }
    
}
