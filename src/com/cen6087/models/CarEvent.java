package com.cen6087.models;

public class CarEvent extends Event {

	private Car car;
	
	public CarEvent(EventTypeEnum type, Car car) {
		super(type);
		// TODO Auto-generated constructor stub
		this.car = car;
	}

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}

}
