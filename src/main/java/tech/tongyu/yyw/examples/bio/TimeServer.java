package tech.tongyu.yyw.examples.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by admin on 2017/6/22.
 */
public class TimeServer {
	public static void main(String[] args){
	    int port = 8080;
	    if(args != null && args.length > 0){
	    	try{
	    		port = Integer.valueOf(args[0]);
			}catch(NumberFormatException e){
	    		port = 8080;
			}
		}

	    try(ServerSocket server = new ServerSocket(port)){
			System.out.println("The time server is start in port: " + port);
			Socket socket = null;
			while(true){
				socket = server.accept();
				new Thread(new TimeServerHandler(socket)).start();
			}
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
}
