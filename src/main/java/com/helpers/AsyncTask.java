package com.helpers;

/**
 * When a task is submitting to thread pool, it require a task name which will
 * be interpreted as a thread name in the pool.
 * 
 * Which mainly helps in debugging multithreaded applications with many async
 * tasks running in the system
 * 
 * @author mallik
 * 
 */
public abstract class AsyncTask implements Runnable {
	String threadName;
	Thread thread = null;
	boolean isStopped = false;

	public AsyncTask(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public void run() {
		String name = Thread.currentThread().getName();
		thread = Thread.currentThread();
		Thread.currentThread().setName(threadName);
		try {
			if (!isStopped) {
				runTask();
			} else {
				// issued stop before thread start
			}
		} finally {
			Thread.currentThread().setName(name);
		}
	}

	public abstract void runTask();

	public void stop() {
		isStopped = true;
		if (thread != null) {
			thread.interrupt();
		}
	}
}