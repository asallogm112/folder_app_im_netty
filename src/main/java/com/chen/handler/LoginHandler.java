package com.chen.handler;

import com.chen.entity.OfflineMsg;
import com.chen.enums.MsgType;
import com.chen.logic.AsyncTask;
import com.chen.logic.SessionManager;
import com.chen.logic.SpringContext;
import com.chen.mapper.extend.MsgReceiptMapperExtend;
import com.chen.mapper.extend.OfflineMsgMapperExtend;
import com.chen.packet.ChatPacket;
import com.chen.packet.LoginPacket;
import com.chen.packet.ReceiptPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@ChannelHandler.Sharable
@Configurable
public class LoginHandler extends SimpleChannelInboundHandler<LoginPacket> {
    
    private int port;
    private String host;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public LoginHandler(RedisTemplate<String, Object> redisTemplate,String host, int port) {
        this.redisTemplate = redisTemplate;
        this.host = host;
        this.port = port;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginPacket login) throws Exception {
        
//        try {
//            redisTemplate.opsForValue().set(login.getUser_id(), host + ":" + port);
//        }catch (Exception e){
//            System.err.println("redis connect failed : " + this.getClass());
//        }
        SessionManager.unlinkChannel(login.getUser_id());
    
        SessionManager.linkChannel(login.getUser_id(), ctx.channel());
        ctx.pipeline().remove(this);
        
        // 对方上线之后 , 给他发送离线消息 (异步)
        //处理离线消息
        AsyncTask task = SpringContext.getBean(AsyncTask.class);
        task.addTask(new Runnable() {
        
            @Override
            public void run() {
                //async
                OfflineMsgMapperExtend offlineMsgMapper = SpringContext.getBean(OfflineMsgMapperExtend.class);
                List<OfflineMsg> offlineMsg_list = offlineMsgMapper.selectListByReceiver_id(login.getUser_id());
    
                for (OfflineMsg offlineMsg : offlineMsg_list) {
                
                    int msg_type = offlineMsg.getMsg_type();
                    MsgType value = MsgType.values()[msg_type];
                
                    Class<? extends ChatPacket> clazz = PacketDecoder.msg_types.get(value);
                    ChatPacket pkt = null;
                    try {
                        pkt = clazz.getConstructor().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (pkt != null) {
                        BeanUtils.copyProperties(offlineMsg, pkt);
                        ctx.channel().writeAndFlush(pkt);
                    }
                }
                
                MsgReceiptMapperExtend receiptMapper = SpringContext.getBean(MsgReceiptMapperExtend.class);
                List<ReceiptPacket> receipt_list = receiptMapper.selectReceiptListBySender_id(login.getUser_id());
                receiptMapper.deleteReceiptListBy_id(login.getUser_id());
    
                for (ReceiptPacket receipt : receipt_list) {
                    ctx.channel().writeAndFlush(receipt);
                }
            }
        });
    }
    
}
