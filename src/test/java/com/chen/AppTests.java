package com.chen;

import com.chen.handler.ChatHandler;
import com.chen.handler.PacketDecoder;
import com.chen.handler.PacketEncoder;
import com.chen.packet.ChatPacket;
import io.netty.channel.ChannelFuture;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetSocketAddress;

@SpringBootTest
class AppTests {
	
	@Test
	void qq(){
	
	}
		
	@Test
	void test() throws Exception {
		
		EmbeddedChannel channel = new EmbeddedChannel(new ChatHandler(),new PacketDecoder(),new PacketEncoder());
		
		ChannelFuture future = channel.connect(new InetSocketAddress(5607));
		
		future.sync();
		
		ChatPacket msg = new ChatPacket();
		msg.setSender_id("111");
		msg.setReceiver_id("444");
		
		future.channel().writeAndFlush(msg);
		
		Thread thread;
		
	}

}
