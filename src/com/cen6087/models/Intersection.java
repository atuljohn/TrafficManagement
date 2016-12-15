package com.cen6087.models;

import java.util.*;

import com.cen6087.models.TrafficLight.LightColor;
import com.cen6087.models.TrafficLight.TrafficDirection;

public class Intersection {

	private int[] index = new int[2];
	private TrafficLight light1, light2;
	// private Intersection[] adjIntersections = new Intersection[2];
	private ArrayList<Intersection> adjIntersections = new ArrayList<Intersection>();
	private TrafficDirection xDirection;
	private TrafficDirection yDirection;
	private boolean INTERSECTION_DEBUG = false;

	// this variable tells us which light is currently GREEN at an intersection
	private TrafficLight activeLight;

	public Intersection(int i, int j, int totalAvenues, int totalStreets) {
		// TODO Auto-generated constructor stub
		this.index[0] = i;
		this.index[1] = j;

		if (i % 2 == 0)
			this.xDirection = TrafficDirection.WE;
		else
			this.xDirection = TrafficDirection.EW;
		if (j % 2 == 0)
			this.yDirection = TrafficDirection.NS;
		else
			this.yDirection = TrafficDirection.SN;
		// "light1" String is for now to test if everything works!
		this.light1 = new TrafficLight(this, xDirection, "light1");
		this.light2 = new TrafficLight(this, yDirection, "light2");

		// Randomly assign 5 ~ 10 seconds as green time for both the lights.
		int tempGreenTime = 5 + new Random().nextInt(6);
		this.light1.setGreenTime(5);
		tempGreenTime = 5 + new Random().nextInt(6);
		this.light2.setGreenTime(5);

		this.light1.setRedTime(this.light2.getGreenYellowTime());
		this.light2.setRedTime(this.light1.getGreenYellowTime());

		if (new Random().nextInt(10000) % 2 == 0) {
			this.light1.setLightStatus(LightColor.RED);
			this.light2.setLightStatus(LightColor.GREEN);
			setActiveLight(this.light2);
		} else {
			this.light1.setLightStatus(LightColor.GREEN);
			this.light2.setLightStatus(LightColor.RED);
			setActiveLight(this.light1);
		}

	}

	public Intersection() {

	}

	public int[] getIndex() {
		return index;
	}

	public void setIndex(int[] index) {
		this.index = index;
	}

	public TrafficLight getLight1() {
		return light1;
	}

	public void setLight1(TrafficLight light1) {
		this.light1 = light1;
	}

	public TrafficLight getLight2() {
		return light2;
	}

	public void setLight2(TrafficLight light2) {
		this.light2 = light2;
	}

	/*
	 * This function sets the active light.
	 */
	public void setActiveLight(TrafficLight active) {
		this.activeLight = active;
		this.activeLight.setLightStatus(LightColor.GREEN);
	}

	/*
	 * returns the value of the light which is currently active
	 */
	public TrafficLight getActiveLight() {
		return this.activeLight;
	}

	/*
	 * updates the current active light and changes the color of the other
	 */
	public void swapActiveLight() {
		if (getActiveLight() == light1) {
			setActiveLight(light2);
			light1.setLightStatus(LightColor.RED);
		} else {
			setActiveLight(light1);
			light2.setLightStatus(LightColor.RED);
		}
	}

	public ArrayList<Intersection> getAdjIntersections() {
		return adjIntersections;
	}

	public void setAdjIntersections(ArrayList<Intersection> adjIntersections) {
		this.adjIntersections = adjIntersections;
	}

	public TrafficDirection getxDirection() {
		return xDirection;
	}

	public void setxDirection(TrafficDirection xDirection) {
		this.xDirection = xDirection;
	}

	public TrafficDirection getyDirection() {
		return yDirection;
	}

	public void setyDirection(TrafficDirection yDirection) {
		this.yDirection = yDirection;
	}

	public void lightControlAlgorithm() {

	}

	@Override
	public String toString() {
		return ("[" + index[0] + "]" + "[" + index[1] + "]");
	}
}