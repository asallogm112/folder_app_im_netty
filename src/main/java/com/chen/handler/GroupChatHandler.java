package com.chen.handler;

import com.chen.enums.NodeMachine;
import com.chen.enums.PacketType;
import com.chen.logic.SessionManager;
import com.chen.packet.ChatPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.SECONDS;

@ChannelHandler.Sharable
public class GroupChatHandler extends SimpleChannelInboundHandler<ChatPacket> {
    
    ExecutorService executorService = new ThreadPoolExecutor(5, 10, 20, SECONDS, new ArrayBlockingQueue<>(15));
    private String host;
    private int port;
    private PacketEncoder encoder = new PacketEncoder();
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public GroupChatHandler(RedisTemplate redisTemplate, String host, int port) {
        this.redisTemplate = redisTemplate;
        this.host = host;
        this.port = port;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatPacket pkt) throws Exception {
        
        String receiver_id = pkt.getReceiver_id();
        
        HashSet<Channel> channels = SessionManager.getGroup(receiver_id);
        
        ArrayList<Object> to_remove = new ArrayList<>();
        if (channels != null) {
            for (Channel c : channels) {
                if (c.isActive()) {
                    c.writeAndFlush(pkt);
                } else {
                    to_remove.add(c);
                }
            }
            channels.removeAll(to_remove);
        }
        
        //别人转发过来的 , 不用再转发了
        if (to_remove != null || pkt.getPacket_type() == PacketType.PacketType_Forward.ordinal()) {
            return;
        }
        pkt.setPacket_type(PacketType.PacketType_Forward.ordinal());
        
        // TODO: 2022/6/29
        HashSet<NodeMachine> other_machines = SessionManager.getOtherMachines(receiver_id);
        
        to_remove.clear();
        if (other_machines != null) {
            for (NodeMachine other_machine : other_machines) {
                if (other_machine.getChannel().isActive()) {
                    other_machine.getChannel().writeAndFlush(pkt);
                } else {
                    to_remove.add(other_machine);
                }
            }
            other_machines.removeAll(to_remove);
        }
        
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                //其他 node 节点
                Set<String> members = redisTemplate.opsForSet().members(receiver_id);
                if (members == null || members.size() == 0) {
                    return;
                }
                
                HashSet<Object> filter_out = new HashSet<>();
                
                String self = GroupChatHandler.this.host + ":" + GroupChatHandler.this.port;
                filter_out.add(self);
                
                if (other_machines != null) {
                    for (String host_colon_port : members) {
                        for (NodeMachine other_machine : other_machines) {
                            if (other_machine.getHost_colon_port() == host_colon_port) { // == 可以避免NPE
                                filter_out.add(host_colon_port);
                                break;
                            }
                        }
                    }
                    members.removeAll(filter_out); //移除本机已经存在的 node
                }
                
                for (String host_colon_port : members) {
                    
                    ChannelFuture future = createNode(host_colon_port);
                    future.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) {
                            if (future.isSuccess()) {
                                NodeMachine nodeMachine = new NodeMachine();
                                nodeMachine.setChannel(future.channel());
                                nodeMachine.setHost_colon_port(host_colon_port);
                                SessionManager.addOtherMachine(receiver_id, nodeMachine);
                                
                                future.channel().writeAndFlush(pkt);
                            }
                        }
                    });
                }
            }
        });
    } //end channelRead0
    
    
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
