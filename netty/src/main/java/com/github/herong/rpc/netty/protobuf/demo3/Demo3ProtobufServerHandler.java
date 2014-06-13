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
package com.github.herong.rpc.netty.protobuf.demo3;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.herong.rpc.netty.protobuf.demo3.Commands.Command;
import com.github.herong.rpc.netty.protobuf.demo3.Request.Heartbeat;
import com.github.herong.rpc.netty.protobuf.demo3.Request.LoginRequest;
import com.google.common.base.CaseFormat;
import com.google.protobuf.GeneratedMessage;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class Demo3ProtobufServerHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory
			.getLogger(Demo3ProtobufServerHandler.class);

	private Channel channel;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("--------------channelActive-------------");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println("--------------收到客户端信息-------------");
		Commands.Command decoded=  (Commands.Command)msg;
		logger.info("receive protobuf decoded message {}" + decoded);
		this.channel = ctx.channel();
		invokeHandle(decoded);
	}

	public void channelRead(ChannelHandlerContext ctx, Commands.Command decoded)
			throws Exception {
		
	}

	private void invokeHandle(Commands.Command decoded) throws Exception {
		Command.CommandType com = decoded.getType();
		String methodName = "handle"
				+ CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,
						com.name());

		for (Method method : getClass().getDeclaredMethods()) {
			if (method.getName().equalsIgnoreCase(methodName)) {
				method.setAccessible(true);
				Class[] types = method.getParameterTypes();
				Class cmdType = types[0];
				Field f = cmdType.getField("cmd");
				GeneratedMessage.GeneratedExtension ext = (GeneratedMessage.GeneratedExtension) f
						.get(decoded);

				method.invoke(this, decoded.getExtension(ext));
				break;
			}
		}
	}

	private void handleLoginReq(LoginRequest login) {
		logger.info("channel {} invoke handleLoginReq : {}", channel, login);
		channel.writeAndFlush(CommandBuilder.buildLoginResponse(0, "success"));
	}

	private void handleHeartbeat(Heartbeat hb) {
		logger.info("channel {} invoke heartbeat {} ", channel, hb);

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		logger.error("Unexpected exception from downstream.", cause);
		ctx.close();
	}
}
