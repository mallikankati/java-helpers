package com.helpers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

public class TestBlockingQueueHelper {

	private static final Logger logger = Logger
			.getLogger(TestBlockingQueueHelper.class.getName());

	class TestListener implements BlockingQueueListener<String> {

		@Override
		public void handleMessage(String message) {
			logger.info(Thread.currentThread().getName() + " consumed message:"
					+ message);
		}
	}

	// @Test
	public void testSimpleBlockingQueueHelper() {
		List<BlockingQueueListener<String>> listeners = new ArrayList<>();
		// depends on how many listeners
		for (int i = 0; i < 2; i++) {
			listeners.add(new TestListener());
		}
		BlockingQueueHelper<String> helper = new BlockingQueueHelper<>(
				"TestPool-", 1000, listeners);
		helper.start();
		// Produce messages to queue;
		for (int i = 0; i < 100000; i++) {
			helper.put("Message - " + i);
		}
		// Wait for messages to drain
		CommonUtil.sleep(2000);
	}

	@Test
	public void testBlockingQueueWithPriorityQueue() {
		List<BlockingQueueListener<String>> listeners = new ArrayList<>();
		// depends on how many listeners
		for (int i = 0; i < 2; i++) {
			listeners.add(new TestListener());
		}

		// If you use comparator in the constructor, inside it will use
		// PriorityQueue to store the messages
		BlockingQueueHelper<String> helper = new BlockingQueueHelper<>(
				"TestPool-", 1000, listeners, new Comparator<String>() {

					@Override
					public int compare(String o1, String o2) {
						return o2.compareTo(o1);
					}
				});
		helper.start();
		// Produce messages to queue;
		for (int i = 0; i < 10000; i++) {
			helper.put("Message - " + i);
		}
		// Wait for messages to drain
		CommonUtil.sleep(2000);
	}

	// Complex test case just extending helper
	// @Test
	public void testBlockingQueuePolling() {
		QueueConsumer helper = new QueueConsumer("Consumer-", 100, 2);
		Thread producer = new Thread(new Producer(helper.getQueue()));
		helper.start();
		producer.start();
		CommonUtil.sleep(2000);
		helper.stop();
		// CommonUtil.sleep(120000);
	}

	private class QueueConsumer extends BlockingQueueHelper<String> {
		// Queue<String> queue = new PriorityQueue<>();

		QueueConsumer(String poolName, int queueCapacity, int workerCount) {
			super(poolName, queueCapacity, workerCount);
			super.setPollEnable(true);
			super.setPollTimeMillis(10);
		}

		@Override
		public void onMessage(String message) {
			if (stopInProgress() || Thread.currentThread().isInterrupted()) {
				logger.info("Thread interrupted");
				return;
			}
			if (message != null) {
				logger.info(Thread.currentThread().getName()
						+ " consumed message :" + message);
				// queue.add(message);
			} else {
				logger.info("Message is null");
				/*
				 * if (!queue.isEmpty()) { logger.info(queue + ""); String str =
				 * queue.peek(); logger.info(str); queue.remove(); }
				 */
			}
		}
	}

	private class Producer implements Runnable {
		BlockingQueue<String> helper;
		int index = 0;

		Producer(BlockingQueue<String> helper) {
			this.helper = helper;
		}

		public void run() {
			while (helper == null) {
				CommonUtil.sleep(10);
			}
			while (helper != null) {
				produce();
			}
		}

		private void produce() {
			try {
				helper.put("Message-" + index);
				index++;
			} catch (Exception e) {
				logger.log(Level.INFO, e.getMessage(), e);
			}
		}
	}
}
