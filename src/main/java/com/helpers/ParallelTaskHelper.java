package com.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParallelTaskHelper {

	private static final Logger logger = Logger
			.getLogger(ParallelTaskHelper.class.getName());

	public static <V> List<V> execute(String taskName, List<Callable<V>> calls) {
		return execute(taskName, calls, calls.size());
	}

	// TODO: This API not maintaining submitted task order instead it returns
	// results in finished order
	public static <V> List<V> execute(String taskName, List<Callable<V>> calls,
			int noOfThreads) {
		List<V> tasks = new ArrayList<>();
		String s = taskName.substring(taskName.length() - 1);
		String tempName = taskName;
		if (!"-".equalsIgnoreCase(s)) {
			tempName += "-";
		}
		ExecutorService es = ThreadUtil.createExecutorService(tempName,
				noOfThreads);
		ExecutorCompletionService<V> ecs = new ExecutorCompletionService<>(es);
		for (Callable<V> task : calls) {
			ecs.submit(task);
		}
		int size = calls.size();
		for (int i = 0; i < size; i++) {
			try {
				V result = ecs.take().get();
				if (result != null) {
					tasks.add(result);
				}
			} catch (Exception e) {
				logger.log(Level.INFO, e.getMessage(), e);
			}
		}
		return tasks;
	}
}
