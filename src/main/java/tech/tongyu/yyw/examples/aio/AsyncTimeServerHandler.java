package tech.tongyu.yyw.examples.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * Created by admin on 2017/6/22.
 */
public class AsyncTimeServerHandler implements Runnable{

	private int port;

	CountDownLatch latch;
	AsynchronousServerSocketChannel asynchronousServerSocketChannel;

	public AsyncTimeServerHandler(int port){
		this.port = port;
		// create an asynchronous server socket channel
		// bind the given port
		try{
			asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
			asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
			System.out.println("The time server is start in port : " + port);
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void run() {
		// latch the thread so as not to let it exit.
		// until we complete all operations we want to do.
		latch = new CountDownLatch(1);
		doAccept();
		try{
			latch.wait();
		}catch (InterruptedException e){
			System.out.println(e.getMessage());
		}
	}

	public void doAccept(){
		// pass and CompletionHandler<AsynchronousSocketChannel, ? super A>
		asynchronousServerSocketChannel.accept(this,
				new AcceptCompletionHandler());
	}
}
