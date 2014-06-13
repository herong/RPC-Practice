package com.github.herong.thrift.server;

import java.util.Map;

import org.apache.thrift.TException;

import com.github.herong.thrift.comm.CommService;
import com.github.herong.thrift.comm.DTO;

public class ServerCommService implements CommService.Iface {

	@Override
	public DTO execute(DTO dto) throws TException {

		Map<String, String> datas = dto.getDatas();
		for (Map.Entry<String, String> data : datas.entrySet()) {
			System.out.println(data.getKey() + "=" + data.getValue());
		}

		DTO respDto = new DTO();
		respDto.putToDatas("key1", "val1");
		respDto.putToDatas("key2", "val2");
		respDto.putToDatas("key3", "val3");
		respDto.putToDatas("key4", "val4");
		respDto.putToDatas("key5", "val5");
		return respDto;
	}

}
