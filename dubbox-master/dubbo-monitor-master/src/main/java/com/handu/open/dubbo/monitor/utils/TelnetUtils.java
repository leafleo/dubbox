package com.handu.open.dubbo.monitor.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.net.telnet.TelnetClient;

public class TelnetUtils {
	 private TelnetClient telnet = new TelnetClient();  
	    private InputStream in; 
	    private PrintStream out;  
	    private String propm = "dubbo>";
	    
	 // 普通用户结束  
	    public TelnetUtils(String ip, int port) {  
	        try {  
	            telnet.connect(ip, port);  
	            in = telnet.getInputStream();  
	            out = new PrintStream(telnet.getOutputStream(),true,"UTF8");  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    }  
	  
	    /** * 读取分析结果 * * @param pattern * @return */  
	    public String readUntil(String pattern) {  
	        try {  
	            char lastChar = pattern.charAt(pattern.length() - 1);  
	            StringBuffer sb = new StringBuffer();  
	           // int read = reader.read();
	            char ch = (char) in.read();  
	            while (true) {  
	                sb.append(ch);  
	                if (ch == lastChar) {  
	                    if (sb.toString().endsWith(pattern)) {  
	                    	sb.delete(sb.length()-propm.length(), sb.length());
	                    	// 处理编码，界面显示乱码问题  
	                        byte[] temp = sb.toString().getBytes("iso8859-1");  
	                        return new String(temp, "GBK");  
	                    }
	                }  
	                ch = (char) in.read();
	                if(sb.toString().contains("HTTP/1.1 400 Bad Request")){
                    	return null;
                    }else if(sb.toString().contains("Connection: close")){
                    	return null;
                    }
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }finally{
	        }
	        return null;  
	    }  
	  
	    /** * 写操作 * * @param value */  
	    public void write(String value) {  
	        try {  
	            out.println(value);  
	            out.flush();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    }  
	  
	    /** * 向目标发送命令字符串 * * @param command * @return */  
	    public String sendCommand(String command) {  
	        try {  
	            write(command);  
	            return readUntil(propm);  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        return null;  
	    }  
	  
	    /** * 关闭连接 */  
	    public void disconnect() {  
	        try {  
	        	out.close();
				in.close();
	            telnet.disconnect();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    }  
	  
	    public static void main(String[] args) {  
	        try {  
	            System.out.println("启动Telnet...");  
	            String ip = "localhost";  
	            int port = 8888;  
	            TelnetUtils telnet = new TelnetUtils(ip, port);  
	            String r1 =  telnet.sendCommand("ls");  
	            String[] services = r1.split("\r\n");
	            for(String servcie : services) {
	            	 String res =  telnet.sendCommand("ls -l " + servcie);  
	            	 String[] split = res.split("\r\n");
	            	 for(String method : split) {
	            		 System.out.println(servcie + "->:" + method);
	            	 }
	            }
	            String r2 = telnet.sendCommand("pwd");  
	            System.out.println("显示结果");  
	            System.out.println(r1);  
	            System.out.println(r2);  
	            telnet.disconnect();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    }  
}
