package tech.tongyu.yyw.examples.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by admin on 2017/6/22.
 */
public class MultiplexerTimeServer implements Runnable {

	//<editor-fold desc="Fields">
	private Selector selector;
	private ServerSocketChannel servChannel;
	private volatile boolean stop;
	//</editor-fold>

	public MultiplexerTimeServer(int port){
		try{
			selector = Selector.open(); // 构造多路复用器Selector
			servChannel = ServerSocketChannel.open();
			servChannel.configureBlocking(false);// 异步非阻塞模式
			servChannel.socket().bind(new InetSocketAddress(port), 1024); // 将ServerSocketChannel设为异步非阻塞模式, backlog设为1024
			servChannel.register(selector, SelectionKey.OP_ACCEPT); // 将ServerSocketChannel注册到Selector, 监听SelectionKey.OP_ACCEPT操作位
			System.out.println("The time server is start in port : " + port);
		}catch(IOException e){
			System.out.println(e.getMessage()); // 可能会遇到端口占用
		}
	}

	public void stop(){
		this.stop = true;
	}

	@Override
	public void run() {
		while(!stop){ // 主循环体 -> 循环遍历selector, 休眠时间为1秒
			try{
				selector.select(1000); // selector每隔1s会被唤醒一次
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectedKeys.iterator();
				SelectionKey key = null;
				while(it.hasNext()){
					key = it.next();
					it.remove();
					try{
						handleInput(key);
					}catch(Exception e){
						if (key != null) {
							key.cancel();
							if(key.channel() != null) key.channel().close();
						}
					}
				}
			}catch(Throwable t){
				System.out.println(t.getMessage());
			}
		}

		if(selector != null){
			try {
				selector.close();
			}catch(IOException e){
				System.out.println(e.getMessage());
			}
		}
	}

	private void handleInput(SelectionKey key) throws IOException{
		if(key.isValid()){
			//处理新接入的请求消息
			// 根据key的操作位进行判断即可获知网络事件的类型
			// 通过ServerSocketChannel的accept接受客户端的连接请求并创建SocketChannel实例
			// 还可以额外设置TCP参数: 如接收和发送缓冲区的大小等
			if(key.isAcceptable()) {
				// Accept the new connection
				ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
				SocketChannel sc = ssc.accept();
				sc.configureBlocking(false);
				// Add the new connection to the selector
				sc.register(selector, SelectionKey.OP_READ);
			}

			// 读取客户端的请求信息
			// 开辟一个1MB的缓冲区. 调用SocketChannel的read方法读取请求码流
			// 由于SocketChannel设置为异步非阻塞模式, 因此其read是非阻塞的
			// 读取到的字节数有三种可能的结果:
			// (1) 返回值大于0:读到了字节, 对字节进行编解码
			// (2) 返回值等于0:没有读取到字节, 属于正常情况, 忽略
			// (3) 返回值为-1: 链路已经关闭, 需要关闭SocketChannel, 释放资源
			// 当读取到码流以后, 进行解码. 首先对readBuffer进行flip操作,
			// 其作用是将缓冲区当前的limit设置为position, position设置为0
			// 用于后续对于缓冲区的读取操作
			// 根据缓冲区可读的字节个数创建字节数组, 调用ByteBuffer的get操作将
			// 缓冲区可读的字节数组复制到新创建的字节数组中
			if(key.isReadable()) {
				// Read the data
				SocketChannel sc = (SocketChannel) key.channel();
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(readBuffer);
				if(readBytes > 0){
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes, "UTF-8");
					System.out.println("The time server receive order : " + body);
					String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new SimpleDateFormat("YYYY-MM-dd").format(new Date()) : "BAD ORDER";
					doWrite(sc, currentTime);
					/*sc.register(selector, SelectionKey.OP_WRITE);*/
				}else if(readBytes < 0) {
					// 对端链路关闭
					key.cancel();
					sc.close();
				}else
					;// 读到0字节, 忽略
			}

			/*if(key.isWritable()){
				SocketChannel sc = (SocketChannel) key.channel();

			}*/
		}
	}

	private void doWrite(SocketChannel channel, String response) throws IOException{
		/*
			将应答消息异步发送给客户端
			首先将字符串编码成字节数组,根据字节数组的容量创建ByteBuffer, 调用ByteBuffer的put操作
			将字节数组复制到缓冲区中, 然后对缓冲区进行flip操作, 最后调用SocketChannel的write方法
			将缓冲区中的字节数组发送出去

			由于SocketChannel是异步非阻塞的, 它并不能保证一次能够把需要发送的字节数组发送完, 此时会出现
			"写半包"问题. 我们需要注册写操作, 不断轮询Selector将没有发送完的ByteBuffer发送完毕，然后可以通过
			ByteBuffer的hasRemain方法判断消息是否发送完成.
		 */
		if(response != null && response.trim().length() > 0){
			byte[] bytes = response.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			channel.write(writeBuffer);
		}
	}
}
