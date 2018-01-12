package com.helpers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class to handle in memory blocking queue. Mainly used in cases where
 * need a in memory buffer which drained by list of workers
 * 
 * Workers size designed by no of listeners constructor argument.
 * 
 * Ex: If user wants to create three workers need to create three
 * BlockingQueueListener
 * 
 * <br>
 * Lists.newList(new BlockingQueueListener(), new BlockingQueueListener(), new
 * BlockingQueueListener());
 * 
 * <br>
 * or <br>
 * 
 * <pre>
 * {@code
 * class SomeConsumer extends BlockingQueueHelper<T> { 
 *    // override one of the constructors and onMessage(..) 
 * }
 * }
 * If PriorityBlockingQueue needs to be used ...need to send Comparator 
 * part of the constructor
 * 
 * Simplest Producer/Consumer should be something like
 * 
 * <pre>
 * {@code
 * 
 * class Consumer implements BlockingQueueListener<String>{
 *      public void handleMessage(String message){
 *          //handle message here
 *      }
 * }
 * 
 * class Producer implements Runnable{
 *     BlockingQueue<String> queue = null;
 *     int index = 0;
 *     public Producer(BlockingQueue<String> queue){
 *        this.queue = queue
 *     }
 *     
 *     public void run(){
 *          while (queue == null){
 *              Thread.sleep(10);
 *          }
 *          while (queue != null && <some other condition to break>){
 *               queue.put("Message :" + index++);
 *          }
 *     }
 * }
 *    ///Operating with three consumers
 *    BlockingQueueHelper<String> helper = new BlockingQueueHelper<>("ThreadName-", 1000, 
 *                           Lists.newList(new Consumer(), new Consumer(), new Consumer());)
 *    helper.start();
 *    
 *    Producer produce = new Producer(helper.getQueue());
 *    produce.start();
 *    Thread.sleep(2000);
 *    helper.stop();
 *    
 * }
 * 
 * @author mallik
 * 
 * @param <T>
 */
public class BlockingQueueHelper<T> {

	private static final Logger logger = Logger
			.getLogger(BlockingQueueHelper.class.getName());

	private BlockingQueue<T> queue = null;

	private Comparator<T> comparator = null;

	private ExecutorService executorService = null;

	private List<BlockingQueueListener<T>> listeners = null;

	private List<QueueDrainHandler> handlers = new ArrayList<>();

	private int workerSize = 0;

	private int queueCapacity = 0;

	private String poolName = null;

	private boolean started = false;

	private boolean pollEnable = false;

	private boolean isStopInProgress;

	private long pollTimeMillis = 0;

	public BlockingQueueHelper(String poolName, int queueCapacity,
			List<BlockingQueueListener<T>> listeners) {
		this.poolName = poolName;
		this.listeners = listeners;
		this.queueCapacity = queueCapacity;
		initializeQueue();
	}

	public BlockingQueueHelper(String poolName, int queueCapacity,
			int workerCount) {
		this.poolName = poolName;
		this.listeners = createListeners(workerCount);
		this.queueCapacity = queueCapacity;
		initializeQueue();
	}

	public BlockingQueueHelper(String poolName, int workerCount) {
		this.poolName = poolName;
		this.listeners = createListeners(workerCount);
		initializeQueue();
	}

	public BlockingQueueHelper(String poolName, int queueCapacity,
			int workerCount, Comparator<T> comparator) {
		this.poolName = poolName;
		this.listeners = createListeners(workerCount);
		this.queueCapacity = queueCapacity;
		this.comparator = comparator;
		initializeQueue();
	}

	public BlockingQueueHelper(String poolName, int queueCapacity,
			List<BlockingQueueListener<T>> listeners, Comparator<T> comparator) {
		this(poolName, queueCapacity, listeners);
		this.comparator = comparator;
		initializeQueue();
	}

	public BlockingQueue<T> getQueue() {
		return this.queue;
	}

	public BlockingQueueHelper(String poolName, BlockingQueue<T> queue,
			List<BlockingQueueListener<T>> listeners) {
		this.poolName = poolName;
		this.queue = queue;
		this.listeners = listeners;
	}

	public void setPollEnable(boolean pollEnable) {
		this.pollEnable = pollEnable;
	}

	public void setPollTimeMillis(long pollTimeMillis) {
		this.pollTimeMillis = pollTimeMillis;
	}

	private void initializeQueue() {
		if (queue == null) {
			if (comparator != null) {
				if (queueCapacity != 0) {
					queue = new PriorityBlockingQueue<>(queueCapacity,
							comparator);
				} else {
					queue = new PriorityBlockingQueue<>(100, comparator);
				}
			} else {
				if (queueCapacity != 0) {
					queue = new LinkedBlockingQueue<T>(queueCapacity);
				} else {
					queue = new LinkedBlockingQueue<T>();
				}
			}
		}
	}

	public void start() {
		if (!started) {
			if (listeners == null)
				throw new RuntimeException("Listeners can not be null");
			if (poolName == null || poolName.length() <= 0)
				throw new RuntimeException("Invalid thread pool name");
			this.workerSize = this.listeners.size();
			this.executorService = ThreadUtil.createExecutorService(poolName,
					workerSize);

			started = true;
			for (int i = 0; i < workerSize; i++) {
				QueueDrainHandler handler = new QueueDrainHandler(
						listeners.get(i));
				this.executorService.submit(handler);
				this.handlers.add(handler);
			}
		} else {
			logger.info("Queue helper already started");
		}
	}

	public void stop() {
		stop(100, 1);
	}

	public void stop(long waitTime, long waitCount) {
		isStopInProgress = true;
		try {
			logger.info("stop fired on name:" + this.poolName + ", qsize:"
					+ queue.size() + ", waitCount:" + waitCount
					+ ", listenerProgress:" + isListenersInProgress());
			if (queue.size() == 0) {
				executorService.shutdown();
			} else {
				int count = 1;
				while (queue.size() != 0 && count <= waitCount
						&& isListenersInProgress()) {
					CommonUtil.sleep(waitTime);
					logger.info("Waiting for shutdown (name:" + this.poolName
							+ ") ... " + count);
					count++;
					interruptHandlers();
				}
				queue.clear();
				executorService.shutdown();
			}
		} finally {
			isStopInProgress = false;
			this.started = false;
		}
	}

	public void waitToFinish(long sleepTime) {
		while (queue.size() != 0 && isListenersInProgress()) {
			// TODO: need to remove
			CommonUtil.sleep(sleepTime);
		}
	}

	public boolean isListenersInProgress() {
		boolean status = (queue.size() != 0 || statusOfListeners());
		return status;
	}

	private boolean statusOfListeners() {
		boolean status = false;
		for (QueueDrainHandler handler : this.handlers) {
			if (handler.inProgress) {
				status = true;
				break;
			}
		}
		return status;
	}

	private void interruptHandlers() {
		for (QueueDrainHandler handler : this.handlers) {
			handler.interrupt();
		}
	}

	public void put(T t) {
		try {
			if (isRunning() && !isStopInProgress) {
				this.queue.put(t);
			} else {
				logger.info("Listeners are not running dequeue messages [" + t
						+ "]");
			}
		} catch (Exception e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}
	}

	public boolean stopInProgress() {
		return !this.started || this.isStopInProgress;
	}

	public boolean offer(T t) {
		boolean status = false;
		try {
			if (isRunning()) {
				status = this.queue.offer(t);
			} else {
				logger.info("Listeners are not running dequeue messages [" + t
						+ "]");
			}
		} catch (Exception e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}
		return status;
	}

	public boolean isRunning() {
		return this.started;
	}

	public boolean isEmpty() {
		return queue.size() == 0;
	}

	public int size() {
		return queue.size();
	}

	private List<BlockingQueueListener<T>> createListeners(int workerCount) {
		List<BlockingQueueListener<T>> listeners = new ArrayList<>();
		for (int i = 0; i < workerCount; i++) {
			listeners.add(createQueueListener());
		}
		return listeners;
	}

	private BlockingQueueListener<T> createQueueListener() {
		return new BlockingQueueListener<T>() {

			@Override
			public void handleMessage(T message) {
				onMessage(message);
			}
		};
	}

	public void onMessage(T message) {
	}

	private class QueueDrainHandler implements Runnable {

		private BlockingQueueListener<T> listener;

		private boolean inProgress = false;

		QueueDrainHandler(BlockingQueueListener<T> listener) {
			this.listener = listener;
		}

		private void interrupt() {
			Thread.currentThread().interrupt();
		}

		@Override
		public void run() {
			try {
				while (!stopInProgress()
						&& !Thread.currentThread().isInterrupted()) {
					if (pollEnable) {
						logger.fine("QueueDrainerHandler startflag:" + started);
						queuePollPolicy();
					} else {
						queueTakePolicy();
					}
				}
			} catch (Exception e) {
				logger.log(Level.INFO, e.getMessage(), e);
			}
		}

		private void queueTakePolicy() throws Exception {
			try {
				T e = queue.take();
				inProgress = true;
				logger.fine("Drained the messge " + e);
				listener.handleMessage(e);
			} catch (Exception ignore) {
				logger.log(Level.INFO, ignore.getMessage(), ignore);
			} finally {
				inProgress = false;
			}
		}

		private void queuePollPolicy() throws Exception {
			long pollTime = pollTimeMillis;
			if (pollTime <= 0) {
				pollTime = 200;
			}
			try {
				T e = queue.poll(pollTime, TimeUnit.MILLISECONDS);
				inProgress = true;
				logger.fine("Drained the messge " + e);
				listener.handleMessage(e);
			} finally {
				inProgress = false;
			}
		}
	}
}
