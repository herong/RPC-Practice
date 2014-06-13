package com.github.herong.thrift.client;

import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.github.herong.thrift.comm.CommService;
import com.github.herong.thrift.comm.DTO;

public class RPCClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TTransport t = null;
		try {
			t = new RPCClient().doAction();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (t != null) {
				t.close();
			}
		}

	}

	public TTransport doAction() throws TException {
		TTransport transport = new TSocket("127.0.0.1", 8080, 500);
		TProtocol protocol = new TBinaryProtocol(transport);
		CommService.Client client = new CommService.Client(protocol);
		transport.open();

		DTO reqDto = new DTO();
		reqDto.putToDatas("rkey1", "val1");
		reqDto.putToDatas("rkey2", "val2");
		reqDto.putToDatas("rkey3", "val3");
		reqDto.putToDatas("rkey4", "val4");
		reqDto.putToDatas("rkey5", "val5");

		DTO respDto = client.execute(reqDto);

		Map<String, String> datas = respDto.getDatas();
		for (Map.Entry<String, String> data : datas.entrySet()) {
			System.out.println(data.getKey() + "=" + data.getValue());
		}

		return transport;

	}
}
