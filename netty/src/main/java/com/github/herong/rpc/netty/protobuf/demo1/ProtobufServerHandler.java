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

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.herong.rpc.protobuf.example.AddressBookProtos.AddressBook;
import com.github.herong.rpc.protobuf.example.AddressBookProtos.Person;
import com.github.herong.rpc.protobuf.example.AddressBookProtos.Person.PhoneNumber;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class ProtobufServerHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = Logger
			.getLogger(ProtobufServerHandler.class.getName());
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("--------------channelActive-------------");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println("--------------收到客户端信息-------------");
		AddressBook addressBook = (AddressBook) msg;
		Person p = addressBook.getPerson(0);
		System.out.println(p.getName() + "," + p.getEmail() + "," + p.getId()
				+ "," + p.getPhoneList());
		
		
		System.out.println("--------------返回信息给客户端-------------");
		AddressBook.Builder builder = AddressBook.newBuilder();
		Person.Builder person = Person.newBuilder();

		person.setName("Hello Welcome!");
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
		ctx.write(builder.build());
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
