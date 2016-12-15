package com.cen6087.models;

public class TrafficLightEvent extends Event {

	private TrafficLight trafficLight;

	public TrafficLightEvent(EventTypeEnum type) {
		super(type);
		// TODO Auto-generated constructor stub
	}

	public TrafficLight getTrafficLight() {
		return trafficLight;
	}

	public void setTrafficLight(TrafficLight trafficLight) {
		this.trafficLight = trafficLight;
	}

}
