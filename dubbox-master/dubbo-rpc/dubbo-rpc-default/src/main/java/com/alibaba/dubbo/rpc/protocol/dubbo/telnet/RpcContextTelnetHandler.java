/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.rpc.protocol.dubbo.telnet;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.utils.ReflectUtils;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.telnet.TelnetHandler;
import com.alibaba.dubbo.remoting.telnet.support.Help;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;

/**
 * RpcContextTelnetHandler
 * 
 * @author leo.aqing
 */
@Activate
@Help(parameter = "cxt_attachment(args)", summary = "Set the rpcCotext attachment.", detail = "Set the rpcCotext attachment.")
public class RpcContextTelnetHandler implements TelnetHandler {
    
    @SuppressWarnings("unchecked")
    public String telnet(Channel channel, String message) {
        if (message == null || message.length() == 0) {
            return "Please input method name, eg: \r\ncxt attachment({\"name\" : \"value\"})\r\n";
        }
        StringBuilder buf = new StringBuilder();
        int i = message.indexOf("(");
        if (i < 0 || ! message.endsWith(")")) {
            return "Invalid parameters, format: attachment(args)";
        }
        String args = message.substring(i + 1, message.length() - 1).trim();
        Map<String, String> attachments = new HashMap<String, String>();
        try {
        	attachments = (Map<String, String>) JSON.parse( args , Map.class);
        } catch (Throwable t) {
            return "Invalid json argument, cause: " + t.getMessage();
        } 
        long start = System.currentTimeMillis();
        RpcContext cxt = RpcContext.getContext();
        cxt.setLocalAddress(channel.getLocalAddress()).setRemoteAddress(channel.getRemoteAddress());
        cxt.getAttachments().putAll(attachments);
        long end = System.currentTimeMillis();
        buf.append("\r\nelapsed: ");
        buf.append(end - start);
        buf.append(" ms.");
        return buf.toString();
    }

}