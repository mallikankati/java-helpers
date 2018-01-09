package com.helpers;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread factory which accepts prefix name to create thread
 * 
 * All thread pools in the system should use this ThreadFactory to create
 * threads
 * 
 * @author mallik
 * 
 */
public class NamedThreadFactory implements ThreadFactory {

	private ThreadGroup threadGroup;
	private String prefix;
	private AtomicInteger threadNum = new AtomicInteger(1);
	boolean prefixEndWithDash = false;

	public NamedThreadFactory(String prefix) {
		this.prefix = prefix;
		if (prefix.charAt(this.prefix.length()-1) == '-') {
			prefixEndWithDash = true;
		}
		threadGroup = Thread.currentThread().getThreadGroup();
	}

	@Override
	public Thread newThread(Runnable r) {
		StringBuffer sb = new StringBuffer();
		sb.append(this.prefix);
		if (!prefixEndWithDash) {
			sb.append("-");
		}
		sb.append(threadNum.getAndIncrement());
		Thread t = new Thread(threadGroup, r, sb.toString());
		return t;
	}
}
