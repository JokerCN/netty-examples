package tech.tongyu.yyw.examples.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2017/6/22.
 */
public class TimeServerHandler implements Runnable{

	private Socket socket;

	public TimeServerHandler(Socket socket){
		this.socket = socket;
	}

	@Override
	public void run() {

		try(BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			PrintWriter out = new PrintWriter(this.socket.getOutputStream())){
			String currentTime = null;
			String body = null;
			while(true){
				body = in.readLine();
				if(body == null) break;
				System.out.println("The time server receive order : " + body);
				currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new SimpleDateFormat("YYYY-MM-dd").format(new Date()) : "BAD ORDER";
				System.out.println(currentTime);
				out.println(currentTime);
				out.flush();
			}
		}catch(IOException e){
			System.out.println(e.getMessage());
			this.socket = null;
		}
	}
}
