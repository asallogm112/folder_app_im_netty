package com.chen;

import com.chen.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.InetAddress;

@Configuration
public class ServerStartup {
    
    @Value("${server.port}")
    private int port;
    
    public void startServer(RedisTemplate redisTemplate) throws Exception {
        
        String host = InetAddress.getLocalHost().getHostAddress();
        
        ServerBootstrap server = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        
        //sharable
        PacketDecoder packetDecoder = new PacketDecoder();
        PacketEncoder packetEncoder = new PacketEncoder();
        ReceiptHandler receiptHandler = new ReceiptHandler();
        LoginHandler loginHandler = new LoginHandler(redisTemplate, host, port);
        GroupEventHandler groupHandler = new GroupEventHandler(redisTemplate, host, port);
        ChatHandler chatHandler = new ChatHandler(redisTemplate, host, port);
        GroupChatHandler groupChatHandler = new GroupChatHandler(redisTemplate, host, port);
    
    
        server.childOption(ChannelOption.TCP_NODELAY, true);
        server.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(20, 20, 20));
        
        server.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipe = ch.pipeline();
                        
                        System.err.println("initChannel");
                        
                        pipe.addLast("FrameDecoder", new FrameDecoder(ch));
                        pipe.addLast("ReadIdleHandler", new ReadIdleHandler());
                        pipe.addLast("packetDecoder", packetDecoder);
                        pipe.addLast("loginHandler", loginHandler);
                        pipe.addLast("groupHandler", groupHandler);
                        pipe.addLast("chatHandler", chatHandler);
                        pipe.addLast("groupChatHandler", groupChatHandler);
                        pipe.addLast("ReceiptHandler", receiptHandler);
                        pipe.addLast("PacketEncoder", packetEncoder);
                    }
                });
        
        try {
            server.bind(port).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}
