package tech.tongyu.yyw.examples.aio;

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

		// start time handler
		AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
	    new Thread(timeServer, "AIO-AsyncTimeServerHandler-001").start();
	}
}
