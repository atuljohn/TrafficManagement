package com.cen6087.models;

public interface ObservableLight {
	public void attachObserverCar(ObserverCar o);

	public void removeObserverCar(ObserverCar o);

	public void notifyAllObserverCar();
}
