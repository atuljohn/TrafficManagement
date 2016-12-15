package com.cen6087.models;

public interface PublisherLight<E> extends ObservableLight {
	public void subscriber (SubscriberLight<String> sub);
	public void publish (E arg);
}
