package tech.tongyu.yyw.examples.bio.advanced;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2017/6/22.
 */
public class TimeServerHandlerExecutePool {

	private ExecutorService executor;

	public TimeServerHandlerExecutePool(int maxPoolSize, int queueSize){
		executor = new ThreadPoolExecutor(
				Runtime.getRuntime().availableProcessors(),
				maxPoolSize,
				120L,
				TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(queueSize));
	}

	public void execute(Runnable task){
		executor.execute(task);
	}
}
