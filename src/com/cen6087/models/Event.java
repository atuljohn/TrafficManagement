package com.cen6087.models;

//Josh

public class Event {
	
	public enum EventTypeEnum {
		CAR, INTERSECTION, TRAFFICLIGHT, CARTOINTERSECTION, CARTOCAR
	}
	
	private EventTypeEnum type;
	private boolean status;
	private long currentTime;
	private long executionTime;
	
	public Event(EventTypeEnum type){
		this.type = type;
	}
	
	public void addToEventList(){
		
	}
	
	public EventTypeEnum getEventType() {
		return type;
	}
	
}

