/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.github.herong.rpc.netty.protobuf.demo1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
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

import com.github.herong.rpc.protobuf.example.AddressBookProtos;
import com.github.herong.rpc.protobuf.example.AddressBookProtos.AddressBook;
import com.github.herong.rpc.protobuf.example.AddressBookProtos.Person;
import com.github.herong.rpc.protobuf.example.AddressBookProtos.Person.PhoneNumber;

/**
 * Echoes back any received data from a client.
 */
public class ProtobufClient {

	private final String host;
	private final int port;

	public ProtobufClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void run() throws Exception {
		// Configure the client.
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch)
								throws Exception {
							ch.pipeline()

									// 解码用
									.addLast("frameDecoder",
											new ProtobufVarint32FrameDecoder())
									// 构造函数传递要解码成的类型
									.addLast(
											"protobufDecoder",
											new ProtobufDecoder(
													AddressBookProtos.AddressBook
															.getDefaultInstance()))
									// 编码用
									.addLast(
											"frameEncoder",
											new ProtobufVarint32LengthFieldPrepender())
									.addLast("protobufEncoder",
											new ProtobufEncoder())
									// 业务逻辑用
									.addLast("handler",
											new ProtobufClientHandler());

						}
					});

			// Start the client.
			ChannelFuture f = b.connect(host, port).sync();
			
			sendMsg(f.channel());
			// Wait until the connection is closed.
			f.channel().closeFuture().sync();
		} finally {
			// Shut down the event loop to terminate all threads.
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		// Print usage if no argument is specified.
		if (args.length < 2 ) {
			System.err.println("Usage: " + ProtobufClient.class.getSimpleName()
					+ " <host> <port> [<first message size>]");
			return;
		}

		// Parse options.
		final String host = args[0];
		final int port = Integer.parseInt(args[1]);

		new ProtobufClient(host, port).run();
	}
	

	public void sendMsg(Channel channel) {
		AddressBook.Builder builder = AddressBook.newBuilder();
		Person.Builder person = Person.newBuilder();

		person.setName("hr");
		person.setEmail("111111@163.com");
		person.setId(1);

		PhoneNumber.Builder phoneNum1 = Person.PhoneNumber.newBuilder();
		/*
		 * PhoneNumber.Builder phoneNum2 = Person.PhoneNumber.newBuilder();
		 * PhoneNumber.Builder phoneNum3 = Person.PhoneNumber.newBuilder();
		 * PhoneNumber.Builder phoneNum4 = Person.PhoneNumber.newBuilder();
		 */
		phoneNum1.setNumber("13111121231");
		phoneNum1.setType(Person.PhoneType.HOME);

		person.addPhone(phoneNum1);
		builder.addPerson(person);
		channel.writeAndFlush(builder.build());
	}

}
