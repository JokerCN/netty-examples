package tech.tongyu.yyw.examples.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by admin on 2017/6/22.
 */
public class TimeClient {

	public static void main(String[] args){
	    int port = 8080;
	    if(args != null && args.length > 0){
	    	try{
	    		port = Integer.valueOf(args[0]);
			}catch(NumberFormatException e){
	    		port = 8080;
			}
		}
		try(
				Socket socket = new Socket("127.0.0.1", port);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
		){
	    	out.println("QUERY TIME ORDER");
			System.out.println("Send order 2 server succeed.");
			String resp = in.readLine();
			System.out.println("Now is : " + resp);
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
}
