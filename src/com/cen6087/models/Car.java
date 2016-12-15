package com.cen6087.models;

import java.util.ArrayList;
import com.cen6087.management.TrafficManagement;
import com.cen6087.models.TrafficLight.TrafficDirection;
import com.cen6087.models.TrafficLight.TrafficLane;

//Josh & Mao & et al

public class Car implements ObserverCar {

	private final static boolean CAR_DEBUG = false;
	private final static boolean ACCEL = false;

	private int id;
	private int arrivalTime;
	private int exitTime;
	private int speed = 10;
	private float acceleration = (float) 5.0;
	private Location location = new Location();
	private ArrayList<TrafficLight> path;
	private int carSize; // and room between cars
	private int roomBetweenCar = 0;
	private TrafficLight currentLight;
	private TrafficLight entryLight;
	private ArrayList<TrafficLight>  turnlist;
	private int [] turnindex;
	private int positionInQueue;
	private int pathNum;
	private float waitingTime = (float) 0.0;
	private TrafficManagement tm;
	private boolean left = false;
	private boolean moving;
	float distanceRemaining;
	float timeToIntersection;
	float startEvent, endEvent;
	private float startWait, endWait;
	private TrafficLane currentLane; // add current lane number. It will change
										// from
	// time to time
	private boolean tested = false;
	private boolean entered;

	private float accelTime;

	private float accelDistance;
	

	/*
	 * Car ID should be generated when the car is generated. And, it should be
	 * something like this: id = arrivalTime + path.arrivalLocation.toString()
	 * When Car will be generated, after that Id should be assigned to Car using
	 * Car.setId(id)
	 */
	public Car(int id, int arrivalTime, int roomBetweenCar, int carSize, ArrayList<TrafficLight> path,
			TrafficManagement tm) {
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.roomBetweenCar = roomBetweenCar;
		this.carSize = carSize;
		this.path = path;
		currentLight = path.get(0);
		this.entryLight = path.get(0);
		this.pathNum = 0;
		this.tm = tm;
		this.distanceRemaining = tm.getDistanceBetweenIntersection();
		moving = true;
		accelTime = speed/acceleration;
		accelDistance = (float) (0.5*acceleration*accelTime*accelTime);
		if(ACCEL)
			timeToIntersection = (distanceRemaining-accelDistance)/speed;
		else
			timeToIntersection = (distanceRemaining)/speed;
		entered = false;
	}

	public Car(int id) {
		this.id = id;
		this.arrivalTime = 000;
		this.roomBetweenCar = 5;
		this.speed = 5;
		this.carSize = 4;
		this.acceleration = 3;
	}

	public void printMe() {
		System.out.println("[car" + id + "]");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getExitTime() {
		return exitTime;
	}

	public void setExitTime(int exitTime) {
		this.exitTime = exitTime;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public float getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(float acceleration) {
		this.acceleration = acceleration;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public TrafficLight getCurrentLight() {
		return this.currentLight;
	}

	public void setCurrentLight(TrafficLight currentLight) {
		this.currentLight = currentLight;
		currentLight.attachObserverCar(this);
	}

	public void moveToNextLight() {
		setCurrentLight(getNextTrafficLight());
		pathNum++;
	}

	public int getPositionInQueue() {
		return positionInQueue;
	}

	public void setPositionInQueue(int positionInQueue) {
		this.positionInQueue = positionInQueue;
	}

	public TrafficLight getEntrylight(){
		return this.entryLight;
	}

	public int getCarSize() {
		return carSize;
	}

	public ArrayList<TrafficLight> getPath() {
		return path;
	}

	//public void setPath(ArrayList<TrafficLight> path) {
	//	this.path = path;
	//}

	public void setCarSize(int carSize) {
		this.carSize = carSize;
	}

	public int getRoomBetweenCar() {
		return roomBetweenCar;
	}

	public void setRoomBetweenCar(int roomBetweenCar) {
		this.roomBetweenCar = roomBetweenCar;
	}

	public void setCurrentlane(TrafficLane lane) {
		this.currentLane = lane;
	}

	public TrafficLane getCurrentlane() {
		return currentLane;
	}

	public void setTurnlist() {
		this.turnlist = new ArrayList<TrafficLight>();
		TrafficDirection entrydirection = this.entryLight.getTrafficDirection();
		for (TrafficLight temp : path) {
			if (temp.getTrafficDirection() != entrydirection) {
				this.turnlist.add(temp);
				entrydirection = temp.getTrafficDirection();
				if (this.turnlist.size() == 2 )
				break;
			}
		}
	}

	public void setTurnindex(){
		 this.turnindex = new int[2];
		 for (int index = 0; index <2; index++)
		 {
			 turnindex[index] = 0;
		 }
		 int i = 0;
		 for (int index =0; index < path.size(); index++)
		 {  
			if (turnlist.size() != 0)
				{ for (TrafficLight item: turnlist)
				  {
					if ( item == path.get(index) )
					{ turnindex[i] = index;
					  i++;
					}
				  }
			   }
			
		 }
		 if(CAR_DEBUG){
			 for (int index = 0; index <2; index++)
			 {
				 System.out.println("the value of index is " +  turnindex[index]);
			 }
		 }
	}
	

	public ArrayList<TrafficLight> getTurnlist() {
		return this.turnlist;
	}
	
	public int [] getTurnindex() {
		return this.turnindex;
	}
	
	public int getIndex(TrafficLight currentLight){
		int index =0;
		for (; index < path.size(); index++)
		{
			if (path.get(index) == currentLight )
			break;
		}
		return index;
	}
	
	public void updatePositioninQueue(TrafficLight currentlight, TrafficLane lane, int n, Car o) {
		if ( o.getCurrentLight() == currentlight && o.getPositionInQueue() != n )
		{
		if (lane == TrafficLane.MIDDLE)
		{    
			  currentlight.getCarsMid().remove(o.getPositionInQueue()-1);
		      currentlight.getCarsMid().set(n-1,o);
		      
		}
		else if (lane == TrafficLane.LEFT)
		{
			currentlight.getCarsLeft().remove(o.getPositionInQueue()-1);
			currentlight.getCarsLeft().set(n-1,o);
		}
	    else 
	    { 
	    	currentlight.getCarsRight().remove(o.getPositionInQueue()-1);
	    	currentlight.getCarsRight().set(n-1,o);
	    }
		
		}
		o.positionInQueue = n;
		
	}
	
	// returns the next item in path. If the path is complete it will return
	// null
	public TrafficLight getNextTrafficLight() {
		// TODO Auto-generated method stub
		return (pathNum + 1) != path.size() ? path.get(pathNum + 1) : null;
	}
   
	// adds an event for when the car approaches the intersection
	public void addEvent() {
		if(left) return;
		moving = true;
		ArrayList<Event> eL = new ArrayList<Event>();
		eL.add(new CarEvent(Event.EventTypeEnum.CARTOINTERSECTION, this));
		int nextTime;
		if(!entered){
			tm.getEventList().addToEventList((long) (tm.getCurentTime()+1), eL);
			nextTime=tm.getCurentTime()+1;
			entered=true;
		}
		else{
			if(ACCEL){
				tm.getEventList().addToEventList((long) (tm.getCurentTime()+accelTime+timeToIntersection+2), eL);
				nextTime=(int) ((int) tm.getCurentTime()+timeToIntersection+2+accelTime);
			} else {
				tm.getEventList().addToEventList((long) (tm.getCurentTime()+timeToIntersection+2), eL);
				nextTime=(int) ((int) tm.getCurentTime()+timeToIntersection+2);
			}
		}
		startEvent = tm.getCurentTime();
		endEvent = (float) nextTime;
		if (CAR_DEBUG) {
			System.out.println("next " + this + " movement: " + nextTime);
		}
	}
	
	public void lightUpdate(int time){
		if(left||moving) return;
		if(time!=0){
			ArrayList<Event> eL = new ArrayList<Event>();
			eL.add(new CarEvent(Event.EventTypeEnum.CARTOINTERSECTION, this));
			tm.getEventList().addToEventList((long) (time+tm.getCurentTime()), eL);
			startEvent = tm.getCurentTime();
			endEvent = (float) (time+tm.getCurentTime());
		}

		startWait = tm.getCurentTime();
		if(ACCEL)
			startWait+=accelTime;
		if(time==0){
			moving=false;
			update();
		}
	}

	// car has approached light, check to see if it needs to move on
	public void processEvent(Event event) {
		if(left) return;
		if(CAR_DEBUG)
			System.out.println(this + " distanceRemainingProcess: " + distanceRemaining);
		if(!tested&&currentLight.getLightStatus()!=TrafficLight.LightColor.RED){
			tested=true;
			int testTime = 0;
			switch (currentLane){
			case LEFT:
				testTime = currentLight.numL;
				break;
			case MIDDLE:
				testTime = currentLight.numM;
				break;
			case RIGHT:
				testTime = currentLight.numR;
				break;
			default:
				break;	
			}
			int gTime = currentLight.getGreenTime() - currentLight.getLightTime();
			if(tm.getCurentTime()-endEvent<testTime&&testTime>gTime){
				ArrayList<Event> eL = new ArrayList<Event>();
				eL.add(new CarEvent(Event.EventTypeEnum.CARTOINTERSECTION, this));
				tm.getEventList().addToEventList((long) (tm.getCurentTime()+(testTime-gTime)), eL);
				waitingTime+=(testTime-gTime);
				//deceleration
				if(ACCEL)
					waitingTime-=accelTime;
				return;
			}
		}
		if(event.getEventType()==Event.EventTypeEnum.CARTOINTERSECTION){
			startWait = tm.getCurentTime();
			if(ACCEL)
				startWait+=accelTime;
			moving = false;
			update();
		}
	}

	// gets called when the car approaches the intersection or when the
	// currentLight is green
	// If currentLight is green or yellow go to next Intersection
	@Override
	public void update() {
		if(left) return;
		if (currentLight.getLightStatus() != TrafficLight.LightColor.RED && moving == false) {
			if (CAR_DEBUG) {
				System.out.println(currentLight.getLightStatus());
			}
			currentLight.removeObserverCar(this);
			moving = true;
			if(tm.getCurentTime()-startWait>0)
				waitingTime += tm.getCurentTime()-startWait;
			tested=false;
		}
	}

	@Override
	public void removeFromGrid() {
		if(left) return;
		// save info, remove from list
		setExitTime(tm.getCurentTime());
		tm.removeCar(this);
		left = true;
	}

	@Override
	public String toString() {
		return "car " + id;
	}
	
	public float getWaitingTime() {
		return waitingTime;
	}
}
