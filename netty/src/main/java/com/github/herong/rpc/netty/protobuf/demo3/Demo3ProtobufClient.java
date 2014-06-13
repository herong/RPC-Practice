package com.github.herong.rpc.netty.protobuf.demo3;


public class Demo3ProtobufClient extends ProtobufClientAbstract {

	public Demo3ProtobufClient() throws Exception {
		super("localhost", 8080, new Demo3ProtobufClientHandler(),
				Commands.Command.getDefaultInstance());
		// TODO Auto-generated constructor stub
	}

	public void request() throws InterruptedException {
		this.channel.writeAndFlush(CommandBuilder.buildTokenLogin("test-token"));
		this.close();
	} 
	
	public static void main(String[] args) throws Exception {
		new Demo3ProtobufClient().request();
	}
}
