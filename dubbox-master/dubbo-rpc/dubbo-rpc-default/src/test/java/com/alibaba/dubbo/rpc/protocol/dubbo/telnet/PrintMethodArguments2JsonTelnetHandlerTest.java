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

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.common.utils.ReflectUtils;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.telnet.TelnetHandler;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcResult;
import com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol;
import com.alibaba.dubbo.rpc.protocol.dubbo.support.CustomArgument;
import com.alibaba.dubbo.rpc.protocol.dubbo.support.DemoService;
import com.alibaba.dubbo.rpc.protocol.dubbo.support.ProtocolUtils;

/**
 * CountTelnetHandlerTest.java
 * 
 * @author tony.chenl
 */
public class PrintMethodArguments2JsonTelnetHandlerTest {

    private static TelnetHandler list = new PrintMethodArguments2JsonTelnetHandler();
    private Channel              mockChannel;
    private Invoker<DemoService> mockInvoker;

    @SuppressWarnings("unchecked")
    @Test
    public void testPamService() throws RemotingException {
        mockInvoker = EasyMock.createMock(Invoker.class);
        EasyMock.expect(mockInvoker.getInterface()).andReturn(DemoService.class).anyTimes();
        EasyMock.expect(mockInvoker.getUrl()).andReturn(URL.valueOf("dubbo://127.0.0.1:20885/demo")).anyTimes();
        EasyMock.expect(mockInvoker.invoke((Invocation) EasyMock.anyObject())).andReturn(new RpcResult("ok")).anyTimes();
        mockChannel = EasyMock.createMock(Channel.class);
        EasyMock.expect(mockChannel.getAttribute("telnet.service")).andReturn("com.alibaba.dubbo.rpc.protocol.dubbo.support.DemoService").anyTimes();
        EasyMock.replay(mockChannel, mockInvoker);
        DubboProtocol.getDubboProtocol().export(mockInvoker);
        String result = list.telnet(mockChannel, "com.alibaba.dubbo.rpc.protocol.dubbo.support.DemoService get(com.alibaba.dubbo.rpc.protocol.dubbo.support.CustomArgument)");
        StringBuffer sb = new StringBuffer();
        sb.append("[{");
        sb.append("\"name\":null,");
        sb.append("\"type\":null,");
        sb.append("}]");
        System.out.println(result.replaceAll("\r\n", ""));
        assertEquals(sb.toString(), result.replaceAll("\r\n", ""));
        
    }

    

}