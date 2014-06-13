package com.github.herong.rpc.netty.protobuf.demo3;


public class Demo3ProtobufServer extends ProtobufServerAbstract {


	public Demo3ProtobufServer() throws Exception {
		super(8080, new Demo3ProtobufServerHandler(),Commands.Command.getDefaultInstance());
	}
	
	public static void main(String[] args) throws Exception {
		new Demo3ProtobufServer();
	}
}
