package tech.tongyu.yyw.examples.bio.advanced;

import tech.tongyu.yyw.examples.bio.TimeServerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by admin on 2017/6/22.
 */
public class TimeServer {
	public static void main(String[] args){
	    int port = 8080;
	    if (args != null && args.length > 0){
	    	try{
	    		port = Integer.valueOf(args[0]);
			}catch(NumberFormatException e){
	    		port = 8080;
			}
		}

		try(
				ServerSocket server = new ServerSocket(port)
				){
			System.out.println("The time server is start in port : " + port);
			Socket socket = null;
			TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50, 10000);//创建IO任务线程池
			while(true){
				socket = server.accept();
				singleExecutor.execute(new TimeServerHandler(socket));
			}
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
}
