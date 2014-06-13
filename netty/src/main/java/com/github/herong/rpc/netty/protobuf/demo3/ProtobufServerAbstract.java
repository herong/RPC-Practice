package com.github.herong.rpc.netty.protobuf.demo3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import com.google.protobuf.MessageLite;

public abstract class ProtobufServerAbstract {

	protected final int port;

	protected ChannelInboundHandlerAdapter handler = null;

	protected MessageLite message = null;

	protected Channel channel = null;

	protected EventLoopGroup bossGroup;
	protected EventLoopGroup workerGroup;

	public ProtobufServerAbstract(int port,
			ChannelInboundHandlerAdapter handler, MessageLite message)
			throws InterruptedException {
		this.port = port;
		this.handler = handler;
		this.message = message;
		init();
	}

	protected void init() throws InterruptedException {
		// Configure the server.
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 100)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline()
								// 解码用
								.addLast("frameDecoder",
										new ProtobufVarint32FrameDecoder())
								// 构造函数传递要解码成的类型
								.addLast("protobufDecoder",
										new ProtobufDecoder(message))
								// 编码用
								.addLast(
										"frameEncoder",
										new ProtobufVarint32LengthFieldPrepender())
								.addLast("protobufEncoder",
										new ProtobufEncoder())
								// 业务逻辑处理
								.addLast("handler", handler);
					}
				});

		// Start the server.
		ChannelFuture f = b.bind(port).sync();
		// Wait until the server socket is closed.
		this.channel = f.channel();
	}

	public void close() throws InterruptedException {
		if (this.channel != null) {
			try {
				this.channel.closeFuture().sync();
			} finally {
				// Shut down all event loops to terminate all threads.
				bossGroup.shutdownGracefully();
				workerGroup.shutdownGracefully();
			}
		}
	}

}
