package com.github.herong.rpc.netty.protobuf.demo3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import com.google.protobuf.MessageLite;

public abstract class ProtobufClientAbstract {
	private final String host;
	private final int port;
	protected EventLoopGroup group = null;
	protected Channel channel = null;
	protected ChannelHandler handler = null;
	protected MessageLite message;

	public ProtobufClientAbstract(String host, int port,ChannelHandler handler,MessageLite message) throws Exception {
		this.host = host;
		this.port = port;
		this.handler = handler;
		this.message = message;
		init();
	}

	protected void init() throws Exception {
		// Configure the client.
		group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline()

								// 解码用
								.addLast("frameDecoder",
										new ProtobufVarint32FrameDecoder())
								// 构造函数传递要解码成的类型
								.addLast(
										"protobufDecoder",
										new ProtobufDecoder(message))
								// 编码用
								.addLast(
										"frameEncoder",
										new ProtobufVarint32LengthFieldPrepender())
								.addLast("protobufEncoder",
										new ProtobufEncoder())
								// 业务逻辑用
								.addLast("handler", handler);

					}
				});

		// Start the client.
		ChannelFuture f = b.connect(host, port).sync();

		// Wait until the connection is closed.
		channel = f.channel();

	}

	public void close() throws InterruptedException {
		if (this.channel != null) {
			try {
				this.channel.closeFuture().sync();
			} finally {
				// Shut down the event loop to terminate all threads.
				group.shutdownGracefully();
			}
		}
	}

}
