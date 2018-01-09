package com.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.junit.Test;

public class TestParallelTaskHelper {

	private static final Logger logger = Logger
			.getLogger(TestParallelTaskHelper.class.getName());

	@Test
	public void testExecute() {
		List<Callable<String>> tasks = new ArrayList<>();
		tasks.add(create("Task1", 3000));
		tasks.add(create("Task2", 2000));
		tasks.add(create("Task3", 4000));
		List<String> results = ParallelTaskHelper.execute("Test", tasks);
		logger.info(results + "");
	}

	private Callable<String> create(final String message, final long sleepTime) {
		Callable<String> c = new Callable<String>() {

			@Override
			public String call() throws Exception {
				logger.info("Executing task: " + Thread.currentThread().getId());
				CommonUtil.sleep(sleepTime);
				return message;
			}
		};
		return c;
	}
}
