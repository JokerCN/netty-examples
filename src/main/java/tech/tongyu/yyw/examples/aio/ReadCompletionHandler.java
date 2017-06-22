package tech.tongyu.yyw.examples.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2017/6/22.
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

	private AsynchronousSocketChannel channel;

	public ReadCompletionHandler(AsynchronousSocketChannel channel){
		if(this.channel == null) this.channel = channel;
	}

	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		attachment.flip();
		byte[] body = new byte[attachment.remaining()];
		attachment.get(body);
		try{
			String req = new String(body, "UTF-8");
			System.out.println("The time server receive order: " + req);
			String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req) ?
					new SimpleDateFormat("YYYY-MM-dd").format(new Date())
					: "BAD ORDER";
			doWrite(currentTime);
		}catch(UnsupportedEncodingException e){
			System.out.println(e.getMessage());
		}
	}

	private void doWrite(String currentTime){
		if(currentTime != null && currentTime.trim().length() > 0){
			byte[] bytes = (currentTime).getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			channel.write(writeBuffer, writeBuffer,
					new CompletionHandler<Integer, ByteBuffer>() {
						@Override
						public void completed(Integer result, ByteBuffer buffer) {
							if(buffer.hasRemaining()) channel.write(buffer, buffer, this);
						}

						@Override
						public void failed(Throwable exc, ByteBuffer attachment) {
							try{
								channel.close();
							}catch(IOException e){
								// ignore on close
							}
						}
					});
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		try{
			this.channel.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
}