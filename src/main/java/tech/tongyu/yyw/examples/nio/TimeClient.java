package tech.tongyu.yyw.examples.nio;

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
		new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001").start();
	}
}
