package com.chen.handler;

import com.chen.entity.MsgReceipt;
import com.chen.enums.MsgStatus;
import com.chen.logic.SessionManager;
import com.chen.logic.SpringContext;
import com.chen.mapper.extend.MsgReceiptMapperExtend;
import com.chen.mapper.extend.OfflineMsgMapperExtend;
import com.chen.packet.ReceiptPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeanUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.SECONDS;

@ChannelHandler.Sharable
public class ReceiptHandler extends SimpleChannelInboundHandler<ReceiptPacket> {
    
    ExecutorService executorService = new ThreadPoolExecutor(5, 10, 20, SECONDS, new ArrayBlockingQueue<>(15));
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ReceiptPacket receipt) throws Exception {
        
        //发送已经 已接收回执, 已读回执,
        System.err.println("回执包 : " + receipt);
        
        try {
            
            if (receipt.getMsg_status() == MsgStatus.MsgStatus_Received.ordinal()) {
                OfflineMsgMapperExtend offlineMsgMapper = SpringContext.getBean(OfflineMsgMapperExtend.class);
                offlineMsgMapper.deleteOfflineMsg(receipt); // 删除数据库 -> msg status
            } else if (receipt.getMsg_status() == MsgStatus.MsgStatus_Read.ordinal()) {
                
                String sender_id = receipt.getSender_id();
                Channel sender_channel = SessionManager.getChannel(sender_id);
                
                if (sender_channel != null) { //此消息 发送者 online
                    receipt.setMsg_status(MsgStatus.MsgStatus_Read.ordinal());
                    sender_channel.writeAndFlush(receipt);
                }
    
                MsgReceiptMapperExtend receiptMapper = SpringContext.getBean(MsgReceiptMapperExtend.class);
    
                MsgReceipt record = new MsgReceipt();
                BeanUtils.copyProperties(receipt, record);
                receiptMapper.insertSelective(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
