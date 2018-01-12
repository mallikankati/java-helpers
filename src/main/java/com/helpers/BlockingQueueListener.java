package com.helpers;

public interface BlockingQueueListener<T> {
	
	public void handleMessage(T message);
	
}
