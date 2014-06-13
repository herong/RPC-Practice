package com.github.herong.thrift.comm.complier;

public class GenRemoteService2 implements IRemoteService {

	@Override
	public String execute(String msg) {
		System.out.println("生成的类名:" + getClass().getName());
		System.out.println("调用方法:execute:" + msg);
		return "resp" + msg;
	}

}
