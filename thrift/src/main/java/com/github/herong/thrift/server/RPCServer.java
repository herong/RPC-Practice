package com.github.herong.thrift.server;

import java.net.InetSocketAddress;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import com.github.herong.thrift.comm.CommService;
import com.github.herong.thrift.comm.CommService.Iface;

public class RPCServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TServer server = null;
		try {
			server = new RPCServer().start();
		} catch (TTransportException e) {
			if (server != null) {
				server.stop();
			}
		}
	}

	public TServer start() throws TTransportException {
		CommService.Processor<Iface> processor = new CommService.Processor<Iface>(
				new ServerCommService());

		TServerTransport serverTransport = new TServerSocket(
				new InetSocketAddress("127.0.0.1", 8080));
		TServer.Args args = new TServer.Args(serverTransport);
		args.processor(processor);
		args.protocolFactory(new TBinaryProtocol.Factory());
		TServer server = new TSimpleServer(args);
		server.serve();
		return server;
	}
}
