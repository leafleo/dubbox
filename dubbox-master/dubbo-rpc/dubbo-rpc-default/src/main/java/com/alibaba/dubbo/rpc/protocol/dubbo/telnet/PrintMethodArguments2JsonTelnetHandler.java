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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.utils.ReflectUtils;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.telnet.TelnetHandler;
import com.alibaba.dubbo.remoting.telnet.support.Help;

/**
 * PrintMethodArguments2JsonTelnetHandler
 * 
 * @author leo.aqing
 */
@Activate
@Help(parameter = "[service] method(args)", summary = "Print the args to json.", detail = "Print the args to json.")
public class PrintMethodArguments2JsonTelnetHandler implements TelnetHandler {
    
    public String telnet(Channel channel, String message) {
        if (message == null || message.length() == 0) {
            return "Please input method name, eg: \r\nprint  xxx.xxx.xxxServcie  pma(java.lang.String,xxx.xxx.Person)";
        }
        StringBuilder buf = new StringBuilder();
        String service = (String) channel.getAttribute(ChangeTelnetHandler.SERVICE_KEY);
        if (service != null && service.length() > 0) {
            buf.append("Use default service " + service + ".\r\n");
        }
        int i = message.indexOf("(");
        if (i < 0 || ! message.endsWith(")")) {
            return "Invalid parameters, format: service.method(args)";
        }
        String method = message.substring(0, i).trim();
        String args = message.substring(i + 1, message.length() - 1).trim();
        i = method.lastIndexOf(" ");
        if (i >= 0) {
            service = method.substring(0, i).trim();
            method = method.substring(i + 1).trim();
        }
        List<Object> list;
        try {
        	 list = (List<Object>) convertArgs(args);
        	 buf.append(JSON.json(list)+"\r\n");
        } catch (Throwable t) {
            return "Invalid method argument, cause: " + t.getMessage();
        }
        return buf.toString();
    }

    /**
     * covert method args 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    public List<Object> convertArgs(String args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    	List<Object> parameTypeList = new ArrayList<Object>();
    	String[] argTypes = args.split(",");
    	for(String type : argTypes) {
    		Class<?> typeClazz = this.forName(type);
    		Object instance = null;
    		try {
    			if (ReflectUtils.isPrimitive(typeClazz)) {
        			if(typeClazz.isAssignableFrom(String.class)) {
    					 Object newInstance = typeClazz.getConstructor(String.class).newInstance("");
    					 instance = newInstance;
    				 } else if(typeClazz.isAssignableFrom(Integer.class)) {
    					 Object newInstance = typeClazz.getConstructor(String.class).newInstance("0");
    					 instance = newInstance;
    				 }else if(typeClazz.isAssignableFrom(Long.class)) {
    						 Object newInstance = typeClazz.getConstructor(String.class).newInstance("0");
    						 instance = newInstance;
    				 } else if(typeClazz.isAssignableFrom(Short.class)) {
    					 Object newInstance = typeClazz.getConstructor(String.class).newInstance("0");
    					 instance = newInstance;
    				 } else if(typeClazz.isAssignableFrom(Byte.class)) {
    					 Object newInstance = typeClazz.getConstructor(String.class).newInstance("0");
    					 instance = newInstance;
    				 } else if (typeClazz.isAssignableFrom(Float.class)) {
    					 Object newInstance = typeClazz.getConstructor(String.class).newInstance("0.0f");
    					 instance = newInstance;
    				 }else if (typeClazz.isAssignableFrom(Double.class)) {
    					 Object newInstance = typeClazz.getConstructor(String.class).newInstance("0.0d");
    					 instance = newInstance;
    				 }else if (typeClazz.isAssignableFrom(Boolean.class)) {
    					 Object newInstance = typeClazz.getConstructor(String.class).newInstance("false");
    					 instance = newInstance;
    				 }else if (typeClazz.isAssignableFrom(Date.class)) {
    					 Date i = (Date)typeClazz.newInstance();
    					 i = new Date();
    					 instance = i;
    				 }else if (typeClazz.isAssignableFrom(Number.class)) {
    					 Object newInstance = typeClazz.getConstructor(BigDecimal.class).newInstance("0.00");
    					 instance = newInstance;
    				 }else if (typeClazz.isAssignableFrom(Character.class)) {
    					 Object newInstance = typeClazz.getConstructor(char.class).newInstance('0');
    					 instance = newInstance;
    				 }else {
    					 instance = new Object();
    				 }
                } else if (typeClazz.isAssignableFrom(Map.class)) {
                	instance = new HashMap();
                } else if (typeClazz.isAssignableFrom(Collection.class)) {
                	instance = new ArrayList();
                } else {
                	instance = typeClazz.newInstance();
                }
			} catch (Exception e) {
				instance = null;
			}
    		parameTypeList.add(instance);
    	}
    	return parameTypeList;
	}
    
	private Class<?> forName(String type) throws ClassNotFoundException {
		Class<?> typeClazz = null;
		if (type.equals("int.class"))
			typeClazz = Integer.class;
		else if (type.equals("boolean.class"))
			typeClazz = Boolean.class;
		else if (type.equals("long.class"))
			typeClazz = Long.class;
		else if (type.equals("float.class"))
			typeClazz = Float.class;
		else if (type.equals("double.class"))
			typeClazz = Double.class;
		else if (type.equals("char.class"))
			typeClazz = Character.class;
		else if (type.equals("byte.class"))
			typeClazz = Byte.class;
		else if (type.equals("short.class"))
			typeClazz = Short.class;
		else if (type.equals("long.class"))
			typeClazz = Long.class;
		if (typeClazz == null)
			typeClazz = Class.forName(type);
		return typeClazz;
	}
}