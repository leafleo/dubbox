package com.handu.open.dubbo.monitor.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.ReflectUtils;
import com.alibaba.dubbo.rpc.RpcContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handu.open.dubbo.monitor.RegistryContainer;
import com.handu.open.dubbo.monitor.utils.TelnetUtils;

@Controller
@RequestMapping("/tests")
public class TestsController {
	private static final String RN = "\r\n";
	private static final Logger logger = LoggerFactory.getLogger(TestsController.class);
	 @Autowired
	 private RegistryContainer registryContainer;
	
	 @RequestMapping(method = RequestMethod.GET)
	 public String index(Model model){
		 long start = System.currentTimeMillis();
		 List<Map<String,Object>> rows = new ArrayList<>();
		 Set<String> urls = registryContainer.getHostPortByProvider();
		 for(String url : urls) {
			 String host = url.split(":")[0] ;
			 int port = Integer.valueOf(url.split(":")[1]);
			 TelnetUtils telnet = new TelnetUtils(host,port);  
			 try {
				 telnet.sendCommand("pwd");  
				 String serviceLine =  telnet.sendCommand("ls");  
				 if(serviceLine != null){
					 String[] services = serviceLine.split(RN);
		             for(String service : services) {
		            	 String methods =  telnet.sendCommand("ls -l " + service);  
		            	 if(methods != null ) {
		            		 Map<String,Object> row = new HashMap<>();
		            		 row.put("host", host);
		            		 row.put("port", String.valueOf(port));
		            		 row.put("service", service);
		            		 row.put("methods", methods.split(RN));
		            		 rows.add(row);
		            	 }
		             }
				 }
			} finally {
				telnet.disconnect();
			}
			
		 }
		 System.out.println("cost:" + (System.currentTimeMillis() - start) );
		 model.addAttribute("rows",rows);
		 return "test/tests";
	 }
	 
	/* @RequestMapping(method = RequestMethod.GET,value="/view")
	 public String view(Model model,String host,Integer port,String service,String method) throws ClassNotFoundException, InstantiationException, IllegalAccessException, JsonProcessingException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		 //java.lang.String redDot()
		 String[] res = method.split(" ");
		 String returnType = res[0];
		 String methodName = res[1];
		 ObjectMapper mapper = new ObjectMapper();
		 List<Object> resultList = new ArrayList<>();
		 if(methodName.length() > 2){
			 String prefix = methodName.substring(methodName.indexOf("(")+1);
			 String[] paramerTypes = prefix.replace(")", "").split(",");
			 if(paramerTypes.length > 0 && !paramerTypes[0].equals("") ){
				 for (String type : paramerTypes) {
					 Class<?> typeClazz = null;
					 if( type .equals( "int.class") )
				            typeClazz = Integer.class;
				        else if( type .equals("boolean.class") )
				            typeClazz = Boolean.class;
				        else  if( type .equals("long.class") )
				            typeClazz = Long.class;
				        else if( type .equals("float.class") )
				            typeClazz = Float.class;
				        else if( type .equals("double.class") )
				            typeClazz = Double.class;
				        else if( type .equals("char.class") )
				            typeClazz = Character.class;
				        else if( type .equals("byte.class") )
				            typeClazz = Byte.class;
				        else if( type .equals("short.class") )
				            typeClazz = Short.class;
				        else if( type .equals("long.class") )
				        	typeClazz = Long.class;
					 if(typeClazz == null)
				        typeClazz = Class.forName(type);
					 boolean primitive = ReflectUtils.isPrimitive(typeClazz);
					 Object value = null;
					 if(primitive) {
						 
						 if(typeClazz.isAssignableFrom(String.class)) {
							 Object newInstance = typeClazz.getConstructor(String.class).newInstance("");
							 value = newInstance;
						 } else if(typeClazz.isAssignableFrom(Integer.class)) {
							 Object newInstance = typeClazz.getConstructor(String.class).newInstance("0");
							 value = newInstance;
						 }else if(typeClazz.isAssignableFrom(Long.class)) {
								 Object newInstance = typeClazz.getConstructor(String.class).newInstance("0");
								 value = newInstance;
						 } else if(typeClazz.isAssignableFrom(Short.class)) {
							 Object newInstance = typeClazz.getConstructor(String.class).newInstance("0");
							 value = newInstance;
						 } else if(typeClazz.isAssignableFrom(Byte.class)) {
							 Object newInstance = typeClazz.getConstructor(String.class).newInstance("0");
							 value = newInstance;
						 } else if (typeClazz.isAssignableFrom(Float.class)) {
							 Object newInstance = typeClazz.getConstructor(String.class).newInstance("0.0f");
							 value = newInstance;
						 }else if (typeClazz.isAssignableFrom(Double.class)) {
							 Object newInstance = typeClazz.getConstructor(String.class).newInstance("0.0d");
							 value = newInstance;
						 }else if (typeClazz.isAssignableFrom(Boolean.class)) {
							 Object newInstance = typeClazz.getConstructor(String.class).newInstance("false");
							 value = newInstance;
						 }else if (typeClazz.isAssignableFrom(Date.class)) {
							 Date i = (Date)typeClazz.newInstance();
							 i = new Date();
							 value = i;
						 }else if (typeClazz.isAssignableFrom(Number.class)) {
							 Object newInstance = typeClazz.getConstructor(BigDecimal.class).newInstance("0.00");
							 value = newInstance;
						 }else if (typeClazz.isAssignableFrom(Character.class)) {
							 Object newInstance = typeClazz.getConstructor(char.class).newInstance('0');
							 value = newInstance;
						 }else {
							 value = null;
						 }
						 resultList.add(value);
					 }
			 }
			 
		 }
		}
		 String writeValueAsString = mapper.writeValueAsString(resultList);
		 model.addAttribute("jsonParameter",writeValueAsString);
		 model.addAttribute("method", method);
		 model.addAttribute("host", host);
		 model.addAttribute("port", port);
		 model.addAttribute("service", service);
		 return "test/view";
	 }*/
	 @RequestMapping(method = RequestMethod.GET,value="/view")
	 public String view(Model model,String host,Integer port,String service,String method) throws ClassNotFoundException, InstantiationException, IllegalAccessException, JsonProcessingException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		 String serviceMethod = service+" " + method;
		 try {
			 TelnetUtils telnet = new TelnetUtils(host,port);
			 telnet.sendCommand("pwd");
			 String res = telnet.sendCommand("pma " + serviceMethod);
			 model.addAttribute("jsonParameter",res.replaceAll("\\{\\}", "null"));
		} catch (Exception e) {
			 model.addAttribute("jsonParameter","[null]");
		}
		 model.addAttribute("method", method);
		 model.addAttribute("host", host);
		 model.addAttribute("port", port);
		 model.addAttribute("service", service);
		 return "test/view";
	 }
	 
	 
	 @RequestMapping(value = "invoke",method =RequestMethod.POST,produces = "application/json;charset=UTF-8")
	 @ResponseBody
	 public String invoke(Model model,String host,int port,String service,String method,String json) throws JsonProcessingException {
		 try {
			 //java.lang.String view(java.lang.Long)
			 int i = method.indexOf(" ");
			 int j = method.lastIndexOf("(");
			 String methodName = method.substring(i+1,j);
			 String invokeString = service+"."+methodName+"(" +(json==null ? "" :  json) + ")";
			 TelnetUtils telnet = new TelnetUtils(host,port);
			 telnet.sendCommand("pwd");
			 String command = "invoke " + invokeString.replace("[", "").replace("]","") + " cxt_attachment<{\"name_\" : \"admin\"}>";
			 System.out.println(command);
			 String res = telnet.sendCommand(command);
			 logger.info("返回结果：" +res.replaceFirst(RN, "").split(RN)[0]);
			 if(res.contains(RN)) {
				 if(res.startsWith(RN)) {
					 res = res.replaceFirst(RN, "");
				 }
				 if(res.contains("Failed")){
					Map<String,String> map = new HashMap<>();
					map.put("Failed",res);
					 ObjectMapper mapper = new ObjectMapper();
					 String writeValueAsString = mapper.writeValueAsString(map);
					 return writeValueAsString;
				 }else{
					 Map<String,Object> map = new HashMap<>();
					 ObjectMapper mapper = new ObjectMapper();
					 String result = res.split(RN)[0];
				 	if(result.startsWith("{")){
				 		 @SuppressWarnings("unchecked")
						Map<String,Object> readValue = mapper.readValue(result, Map.class);
						 map.put("结果",readValue);
						 map.put("时间",res.split(RN)[1]);
				 	}else if(result.startsWith("[")){
						 return result;
				 	}else if(result.equals("null")) {
				 		 map.put("结果","null");
				 	}
					 String writeValueAsString = mapper.writeValueAsString(map);
					 return writeValueAsString;
				 }
			 }
			 return  res ;
		} catch (Exception e) {
			e.printStackTrace();
			 Map<String,Object> map = new HashMap<>();
			 ObjectMapper mapper = new ObjectMapper();
			 map.put("异常",e.getMessage());
			 return mapper.writeValueAsString(map);
		}
		
	 }
}
