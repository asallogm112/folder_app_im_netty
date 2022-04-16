package com.chen;

import com.chen.handler.*;
import com.chen.packet.MyLogging;
import com.chen.packet.PacketType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServerStartup {
    
    @Value("${server.port}")
    private int port;
    
    public void startServer() {
        PacketType.initPacketTypes();
        
        ServerBootstrap server = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        
        PacketDecoder packetDecoder = new PacketDecoder();
        PacketEncoder packetEncoder = new PacketEncoder();
        ReceiptHandler receiptHandler = new ReceiptHandler();
        LoginHandler loginHandler = new LoginHandler();
        ChatHandler chatHandler = new ChatHandler();
    
        server.childOption(ChannelOption.TCP_NODELAY, true);
        server.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(20, 20, 20));
    
        server.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipe = ch.pipeline();
                        
                        System.err.println("initChannel");
    
                        pipe.addLast("logging", new MyLogging());
                        pipe.addLast("ReadIdleHandler", new ReadIdleHandler());
                        pipe.addLast("FrameDecoder", new FrameDecoder());
                        pipe.addLast("PacketDecoder", packetDecoder);
                        pipe.addLast("LoginHandler", loginHandler);
                        pipe.addLast("ChatHandler", chatHandler);
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
