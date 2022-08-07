package com.chen.handler;

import com.chen.entity.OfflineMsg;
import com.chen.enums.MsgStatus;
import com.chen.enums.PacketType;
import com.chen.logic.SessionManager;
import com.chen.logic.SpringContext;
import com.chen.mapper.OfflineMsgMapper;
import com.chen.mapper.extend.OfflineMsgMapperExtend;
import com.chen.packet.ChatPacket;
import com.chen.packet.ReceiptPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.SECONDS;

@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<ChatPacket> {
    
    AttributeKey<String> key = AttributeKey.valueOf("user_id");
    ExecutorService executorService = new ThreadPoolExecutor(5, 10, 20, SECONDS, new ArrayBlockingQueue<>(15));
    private String host;
    private int port;
    private PacketEncoder encoder = new PacketEncoder();
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public ChatHandler(RedisTemplate redisTemplate, String host, int port) {
        this.redisTemplate = redisTemplate;
        this.host = host;
        this.port = port;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatPacket pkt) {
        
        System.err.println("currentThread : " + Thread.currentThread());
        
        pkt.setToken(null);
        pkt.setMsg_status(MsgStatus.MsgStatus_Sent.ordinal());
        String receiver_id = pkt.getReceiver_id();
    
        // TODO: 2022/6/29  bug to fix, 群聊 和 单聊
        //receipt 单聊 群聊, 给自己回执(已发送),否则一直在转圈圈
        ReceiptPacket receipt = new ReceiptPacket();
        BeanUtils.copyProperties(pkt, receipt);
        ctx.channel().writeAndFlush(receipt);
    
        // TODO: 2022/7/21  live group chat
        //PacketType_Live 不需要存库, 也不需要 发回执
        if (pkt.getPacket_type() != PacketType.PacketType_Live.ordinal()) {
            
            executorService.execute(new Runnable() {
                @Override
                public void run() {
            
                    try {
                        // 插入数据库,防止漏消息 , 对方接受成功后, 发送消息已收到回执 ->去删除数据库
                        OfflineMsgMapper offlineMsgMapper = SpringContext.getBean(OfflineMsgMapperExtend.class);
                        OfflineMsg record = new OfflineMsg();
                        BeanUtils.copyProperties(pkt, record);
                        offlineMsgMapper.insertSelective(record); // 不管是否在线, 插入离线消息,防止丢消息
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        
        if (receiver_id.startsWith("gid:")) { //群聊, 转发到 群聊handler
            
            ctx.fireChannelRead(pkt);
            
        } else { //单聊
            Channel channel = SessionManager.getChannel(receiver_id);
            if (channel != null) {
                if (channel.isActive()) {
                    channel.writeAndFlush(pkt);
                } else {
                    String user_id = channel.attr(key).get();
                    SessionManager.unlinkChannel(user_id);
                }
            } else {
                //别人转发过来的 , 不用再转发了
                //这种情况, 应该是 对方不在线, 要发送 apns 或者 安装推送
                if (pkt.getPacket_type() == PacketType.PacketType_Forward.ordinal()) {
                    return;
                }
                
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        String host_colon_port = null;
                        try {
                            host_colon_port = redisTemplate.opsForValue().get(receiver_id);
    
                        }catch (Exception e){
                            // TODO: 2022/6/29 apns or android push
                            // redis连接异常
        
                        }
                        if (StringUtils.isEmpty(host_colon_port)) {
                            // TODO: 2022/6/29 apns or android push
                            
                        } else {
                            ChannelFuture future = createNode(host_colon_port);
                            future.addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) {
                                    if (future.isSuccess()) {
                                        pkt.setPacket_type(PacketType.PacketType_Forward.ordinal());
                                        future.channel().writeAndFlush(pkt);
                                        SessionManager.linkChannel(receiver_id, future.channel());
                                    }
                                }
                            });
                        }
                    }
                });
            } //end 单聊
        }
    }
    
    private ChannelFuture createNode(String key) {
        
        if (StringUtils.isEmpty(key) || !key.contains(":")) {
            return null;
        }
        int i = key.indexOf(":");
        String host = key.substring(0, i);
        int port = Integer.parseInt(key.substring(i + 1, key.length()));
        
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        
        bootstrap.group(group).channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("encoder", encoder);
                ch.pipeline().addLast("", new WriteIdleHandler());
            }
        });
        
        ChannelFuture future = bootstrap.connect(host, port);
        future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        return future;
    }
}
