package com.cen6087.models;

public class IntersectionEvent extends Event {

	private Intersection intersection;

	public IntersectionEvent(EventTypeEnum type) {
		super(type);
		// TODO Auto-generated constructor stub
	}

	public Intersection getTrafficLight() {
		return intersection;
	}

	public void setTrafficLight(Intersection intersection) {
		this.intersection = intersection;
	}

}
