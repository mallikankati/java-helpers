package com.helpers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public final class ThreadUtil {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ThreadUtil.class.getName());
	
	public static ExecutorService createExecutorService(String poolName,
			int poolSize) {
		ExecutorService executorService = Executors.newFixedThreadPool(
				poolSize, new NamedThreadFactory(poolName));
		return executorService;
	}
}
