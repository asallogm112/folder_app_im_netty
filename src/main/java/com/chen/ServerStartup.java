package com.chen;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.chen.handler.ChatHandler;
import com.chen.handler.LoginHandler;
import com.chen.handler.PacketDecoder;
import com.chen.handler.PacketEncoder;
import com.chen.handler.ReceiptHandler;
import com.chen.handler.TimeoutHandler;
import com.chen.packet.PacketType;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

@Component
public class ServerStartup {

	@Value("${server.port}")
	private int port;

	public void start() {
		PacketType.initPacketTypes();

		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup work = new NioEventLoopGroup();

		ServerBootstrap server = new ServerBootstrap();
		server.group(boss, work).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<Channel>() {

			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipe = ch.pipeline();

				int maxFrameLength = 1024 * 1024 * 10;
				int idleSeconds = 30;

				pipe.addFirst("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(maxFrameLength, 4, 4));
				pipe.addLast("IdleStateHandler", new IdleStateHandler(idleSeconds, 0, 0)); // idleSeconds = 30秒 no read event = 超时
				pipe.addLast("PacketDecoder", new PacketDecoder());
				pipe.addLast("PacketEncoder", new PacketEncoder());
				pipe.addLast("LoginHandler", new LoginHandler());
				pipe.addLast("ChatHandler", new ChatHandler());
				pipe.addLast("ReceiptHandler", new ReceiptHandler());
				pipe.addLast("TimeoutHandler", new TimeoutHandler());

			};
		});

		server.childOption(ChannelOption.TCP_NODELAY, true);
		server.childOption(ChannelOption.SO_BACKLOG, 1024);

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
