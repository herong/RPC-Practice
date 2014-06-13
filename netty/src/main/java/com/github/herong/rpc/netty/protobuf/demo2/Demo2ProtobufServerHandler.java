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
package com.github.herong.rpc.netty.protobuf.demo2;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.omg.CORBA.UserException;

import com.github.herong.rpc.netty.protobuf.demo2.Message.UserBaseInfo;
import com.github.herong.rpc.netty.protobuf.demo2.Message.UserExtInfo;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class Demo2ProtobufServerHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = Logger
			.getLogger(Demo2ProtobufServerHandler.class.getName());

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("--------------channelActive-------------");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println("--------------收到客户端信息-------------");
		Message.DTO dto = (Message.DTO) msg;

		switch (dto.getType()) {
		case T0001:
			UserBaseInfo userBaseInfo = dto.getM001().getUserBaseInfo();
			logger.info(userBaseInfo.getNickName() + ","
					+ userBaseInfo.getEmail() + "," + userBaseInfo.getPhone());
			break;
		case T0002:
			UserExtInfo userExtInfo = dto.getM002().getUserExtInfo();
			logger.info(userExtInfo.getName() + "," + userExtInfo.getBirthday());
			break;
		default:
			break;
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		logger.log(Level.WARNING, "Unexpected exception from downstream.",
				cause);
		ctx.close();
	}
}
