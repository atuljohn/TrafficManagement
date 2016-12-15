package com.cen6087.models;

/*
 * Keeps the X,Y value of a Location.
 * X-axis = Avenue
 * Y-axis = Street
 */
public class Location {
	private TrafficLight light;
	private int lane;
	private int positionInQueue;

	public Location() {
		super();
	}

	public int getPositionInQueue() {
		return positionInQueue;
	}

	public void setPositionInQueue(int positionInQueue) {
		this.positionInQueue = positionInQueue;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return light + " QueuePosition: " + positionInQueue;
	}

}
