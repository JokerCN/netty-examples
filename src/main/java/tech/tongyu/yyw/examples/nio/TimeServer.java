package tech.tongyu.yyw.examples.nio;

import java.util.Scanner;

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

		MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
	    new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();
		Scanner sc = new Scanner(System.in);
		if(sc.next().isEmpty());
	}
}
