package com.github.herong.rpc.netty.protobuf.demo3;

import com.github.herong.rpc.netty.protobuf.demo3.Commands.Command;
import com.github.herong.rpc.netty.protobuf.demo3.Request.LoginRequest;
import com.github.herong.rpc.netty.protobuf.demo3.Response.LoginResponse;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;


public class CommandBuilder {
    public static final ExtensionRegistry registry= ExtensionRegistry.newInstance();

    static {
        Commands.registerAllExtensions(registry);
    }

    static <Type> Command wrap(Command.CommandType type, GeneratedMessage.GeneratedExtension<Command, Type> extension, Type cmd) {
        return Command.newBuilder().setType(type).setExtension(extension, cmd).build();
    }

    public static Command buildLoginResponse(int value, String reason) {
        return wrap(Command.CommandType.LOGIN_RESP, LoginResponse.cmd, LoginResponse.newBuilder().setCode(value).setReason(reason).build());
    }

    public static Command buildPlainLogin(String username, String password) {
        return wrap(Command.CommandType.LOGIN_REQ, LoginRequest.cmd, LoginRequest.newBuilder().setUsername(username).setPassword(password).build());
    }

    public static Command buildTokenLogin(String token) {
        return wrap(Command.CommandType.LOGIN_REQ, LoginRequest.cmd, LoginRequest.newBuilder().setToken(token).build());
    }

    public static Command buildHeartbeat() {
        return Command.newBuilder().setType(Command.CommandType.HEARTBEAT).build();
    }


}
