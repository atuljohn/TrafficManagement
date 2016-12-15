package com.cen6087.models;

import java.util.*;


import com.cen6087.management.EventList;
import com.cen6087.models.Event.EventTypeEnum;
import com.cen6087.models.TrafficLightEvent;
import com.cen6087.models.Car;

public class TrafficLight implements ObservableLight,SubscriberLight<String> {

	// debugging purposes
	public final static boolean TRAFFIC_LIGHT_DEBUG = false;

	private int redTime;
	private int greenTime;
	private int yellowTime = TrafficManagementStatic.yellowTime;
	// private ArrayList<ObserverCar> cars = new ArrayList<ObserverCar>();
	private ArrayList<ObserverCar> carsLeft = new ArrayList<ObserverCar>();
	private ArrayList<ObserverCar> carsMid = new ArrayList<ObserverCar>();
	private ArrayList<ObserverCar> carsRight = new ArrayList<ObserverCar>();
	private int laneLimit = TrafficManagementStatic.laneLimit;
	private LightColor lightStatus;
	private long timestamp;
	private TrafficLight couple;
	private Intersection intersection;
	private TrafficDirection direction;
	private String lightNumber;
	public int numR=0, numL=0, numM=0;

	// This timer is used to count number of seconds is left for a Color.
	private int lightTime;

	// Publisher Subscriber stuff
	private List<SubscriberLight<String>> subscribers;
	
	public ArrayList<Event> publish (String data) {
		// add to event list from here.
		// return an ArrayList of Events
		ArrayList<Event> list = new ArrayList<Event>();
		for (SubscriberLight<String> sub : subscribers) {
			int[] interNumber = ((TrafficLight) sub).getIntersection().getIndex();
			Intersection temp = new Intersection(interNumber[0],interNumber[1],0,0);
			sub.getPublication(data);
			TrafficLightEvent e1 = new TrafficLightEvent(EventTypeEnum.TRAFFICLIGHT);
			if ( ((TrafficLight) sub).getLightNumber().equals("light1") ) {
				if(TRAFFIC_LIGHT_DEBUG)
					System.out.println(interNumber[0] + "," + interNumber[1] + " " + ((TrafficLight) sub).getLightNumber());
				e1.setTrafficLight(temp.getLight1());
			}
			else if (((TrafficLight) sub).getLightNumber().equals("light2")) {
				if(TRAFFIC_LIGHT_DEBUG)
					System.out.println(interNumber[0] + "," + interNumber[1] + " " + ((TrafficLight) sub).getLightNumber());
				e1.setTrafficLight(temp.getLight2());
			}
			list.add(e1);
			
		}
		return list;
	}
	
	public void subscribe (SubscriberLight<String> sub) {
		if ( sub != null)
			subscribers.add(sub); // mutation
	}
	
	
	public TrafficLight(Intersection intersection, TrafficDirection direction, String lightNumber) {
		super();
		this.intersection = intersection;
		this.direction = direction;
		this.lightNumber = lightNumber;
		// Publisher Subscriber stuff
	    subscribers = new ArrayList<SubscriberLight<String>>();
		
	}

	public TrafficLight() {

	}

	public enum LightColor {
		RED, YELLOW, GREEN;
	}

	public enum TrafficDirection {
		NS, SN, EW, WE;
	}

	public enum TrafficLane {
		LEFT, MIDDLE, RIGHT;
	}

	public TrafficDirection getTrafficDirection() {
		return direction;
	}

	public void setLightNumber(String lightNumber) {
		this.lightNumber = lightNumber;
	}

	public String getLightNumber() {
		return lightNumber;
	}

	public void setGreenTime(int green) {
		this.greenTime = green;
	}

	public int getGreenTime() {
		return this.greenTime;
	}

	public void setYellowTime(int yellow) {
		this.yellowTime = yellow;
	}

	public int getYellowTime() {
		return this.yellowTime;
	}

	public void setLightTime(int lightTime) {
		this.lightTime = lightTime;
	}

	public int getLightTime() {
		return this.lightTime;
	}

	public boolean decreaseLightTime() {
		return --this.lightTime > 0 ? true : false;
	}

	/*
	 * This function calculates the sum of greenTime and yellowTime and returns
	 * the value.
	 */
	public int getGreenYellowTime() {
		return (greenTime + yellowTime);
	}

	public void setRedTime(int red) {
		this.redTime = red;
	}

	public int getRedTime() {
		return this.redTime;
	}

	public void setLightStatus(LightColor status) {
		lightStatus = status;
		switch (lightStatus) {
		case RED:
			setLightTime(getRedTime());
			break;
		case YELLOW:
			setLightTime(getYellowTime());
			break;
		case GREEN:
			setLightTime(getGreenTime());
			int time = greenTime;
			numR = carsRight.size();
			numL = carsLeft.size();
			numM = carsMid.size();
			for(int i = 0; i<carsLeft.size()&&time>0;i++){
				time--;
				((Car) carsLeft.get(i)).lightUpdate(i);
			}
			for(int i = 0; i<carsRight.size()&&time>0;i++){
				time--;
				((Car) carsRight.get(i)).lightUpdate(i);
			}
			for(int i = 0; i<carsMid.size()&&time>0;i++){
				time--;
				((Car) carsMid.get(i)).lightUpdate(i);
			}

			break;
		default:
			break;
		}

	}

	public LightColor getLightStatus() {
		return lightStatus;
	}

	public Intersection getIntersection() {
		return intersection;
	}

	public void setIntersection(Intersection intersection) {
		this.intersection = intersection;
	}

	public void updateCarWaitingQueue() {

	}

	// Once max limit is hit notify list
	public void notifyEventList() {

	}

	// When a car is assigned to this light this is the method that gets called
	// updated the multiple lanes for each light
	@Override
	public void attachObserverCar(ObserverCar o) {
		// TODO Auto-generated method stub
		// Add into different lanes
		((Car) o).setTurnlist();
		TrafficLight currentl = ((Car) o).getCurrentLight();
		TrafficLight entry = ((Car) o).getEntrylight();
		TrafficLight list; 
		if(TRAFFIC_LIGHT_DEBUG)
			System.out.println("The turn list size is "+ ((Car) o).getTurnlist().size() );
		TrafficLight list1;
		TrafficLight list2;
	  //  System.out.println("The turn list size is "+ ((Car) o).getTurnlist().size() );
		if (((Car) o).getTurnlist() == null || ((Car) o).getTurnlist().size() == 0) {
			 ((Car) o).setCurrentlane(TrafficLane.MIDDLE); // no turn
			carsMid.add(o);
		//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane(),carsMid.size(), ((Car) o));
		} else if (((Car) o).getTurnlist().size() == 1) {
			
			list = ((Car) o).getTurnlist().get(0);
		   if (entry.getTrafficDirection() == TrafficDirection.EW
					&& list.getTrafficDirection() == TrafficDirection.SN) {
				((Car) o).setCurrentlane(TrafficLane.RIGHT); // turn right
				carsRight.add(o);
			//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
			} else if (entry.getTrafficDirection() == TrafficDirection.EW
					&& list.getTrafficDirection() == TrafficDirection.NS) {
				((Car) o).setCurrentlane(TrafficLane.LEFT); // turn left
				carsLeft.add(o);
			//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
			} else if (entry.getTrafficDirection() == TrafficDirection.WE
					&& list.getTrafficDirection() == TrafficDirection.SN) {
				((Car) o).setCurrentlane(TrafficLane.LEFT); // turn left
				carsLeft.add(o);
			//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
			} else if (entry.getTrafficDirection() == TrafficDirection.WE
					&& list.getTrafficDirection() == TrafficDirection.NS) {
				((Car) o).setCurrentlane(TrafficLane.RIGHT); // turn right
				carsRight.add(o);
			//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
			} else if (entry.getTrafficDirection() == TrafficDirection.NS
					&& list.getTrafficDirection() == TrafficDirection.EW) {
				((Car) o).setCurrentlane(TrafficLane.RIGHT); // turn right
				carsRight.add(o);
			//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
			} else if (entry.getTrafficDirection() == TrafficDirection.NS
					&& list.getTrafficDirection() == TrafficDirection.WE) {
				((Car) o).setCurrentlane(TrafficLane.LEFT); // turn left
				carsLeft.add(o);
			//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
			} else if (entry.getTrafficDirection() == TrafficDirection.SN
					&& list.getTrafficDirection() == TrafficDirection.WE) {
				((Car) o).setCurrentlane(TrafficLane.RIGHT); // turn right
				carsRight.add(o);
			//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
			} else if (entry.getTrafficDirection() == TrafficDirection.SN
					&& list.getTrafficDirection() == TrafficDirection.EW) {
				((Car) o).setCurrentlane(TrafficLane.LEFT); // turn left
				carsLeft.add(o);
			//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
			}
		    
		//   ((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane(), );
		
		    
			}

		
		
		else {// 2 turns
			  list1 = ((Car) o).getTurnlist().get(0);
			  list2 = ((Car) o).getTurnlist().get(1);
			  if ( currentl.getLightNumber() != list1.getLightNumber() && ((Car) o).getIndex(currentl) < ((Car) o).getIndex(list1)   ) 
               {  
			   if (entry.getTrafficDirection() == TrafficDirection.EW
						&& list1.getTrafficDirection() == TrafficDirection.SN) {
					((Car) o).setCurrentlane(TrafficLane.RIGHT); // turn right
					carsRight.add(o);
				//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
				} else if (entry.getTrafficDirection() == TrafficDirection.EW
						&& list1.getTrafficDirection() == TrafficDirection.NS) {
					((Car) o).setCurrentlane(TrafficLane.LEFT); // turn left
					carsLeft.add(o);
				//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
				} else if (entry.getTrafficDirection() == TrafficDirection.WE
						&& list1.getTrafficDirection() == TrafficDirection.SN) {
					((Car) o).setCurrentlane(TrafficLane.LEFT); // turn left
					carsLeft.add(o);
				//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
				} else if (entry.getTrafficDirection() == TrafficDirection.WE
						&& list1.getTrafficDirection() == TrafficDirection.NS) {
					((Car) o).setCurrentlane(TrafficLane.RIGHT); // turn right
					carsRight.add(o);
				//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
				} else if (entry.getTrafficDirection() == TrafficDirection.NS
						&& list1.getTrafficDirection() == TrafficDirection.EW) {
					((Car) o).setCurrentlane(TrafficLane.RIGHT); // turn right
					carsRight.add(o);
				//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
				} else if (entry.getTrafficDirection() == TrafficDirection.NS
						&& list1.getTrafficDirection() == TrafficDirection.WE) {
					((Car) o).setCurrentlane(TrafficLane.LEFT); // turn left
					carsLeft.add(o);
				//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
				} else if (entry.getTrafficDirection() == TrafficDirection.SN
						&& list1.getTrafficDirection() == TrafficDirection.WE) {
					((Car) o).setCurrentlane(TrafficLane.RIGHT); // turn right
					carsRight.add(o);
				//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
				} else if (entry.getTrafficDirection() == TrafficDirection.SN
						&& list1.getTrafficDirection() == TrafficDirection.EW) {
					((Car) o).setCurrentlane(TrafficLane.LEFT); // turn left
					carsLeft.add(o);
				//	((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
				}
             }
			/*  else if (currentl == list1 )
			  {
				  System.out.println((Car) o + "current reachs turn 1 " + ((Car) o).getIndex(list1) );
			  }
			  else if (currentl == list2)
			  {
				  System.out.println((Car) o + "current reachs turn 2 " + ((Car) o).getIndex(list2) );
			  }
			*/
			  else if (((Car) o).getIndex(currentl) >= ((Car) o).getIndex(list1))
			  {
				  if (list1.getTrafficDirection() == TrafficDirection.EW
							&& list2.getTrafficDirection() == TrafficDirection.SN) {
						((Car) o).setCurrentlane(TrafficLane.RIGHT); // turn right
						carsRight.add(o);	
					} else if (list1.getTrafficDirection() == TrafficDirection.EW
							&& list2.getTrafficDirection() == TrafficDirection.NS) {
						((Car) o).setCurrentlane(TrafficLane.LEFT); // turn left
						carsLeft.add(o);
						//((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
					} else if (list1.getTrafficDirection() == TrafficDirection.WE
							&& list2.getTrafficDirection() == TrafficDirection.SN) {
						((Car) o).setCurrentlane(TrafficLane.LEFT); // turn left
						carsLeft.add(o);
						//((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
					} else if (list1.getTrafficDirection() == TrafficDirection.WE
							&& list2.getTrafficDirection() == TrafficDirection.NS) {
						((Car) o).setCurrentlane(TrafficLane.RIGHT); // turn right
						carsRight.add(o);
						//((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
					} else if (list1.getTrafficDirection() == TrafficDirection.NS
							&& list2.getTrafficDirection() == TrafficDirection.EW) {
						((Car) o).setCurrentlane(TrafficLane.RIGHT); // turn right
						carsRight.add(o);
						//((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
					} else if (list1.getTrafficDirection() == TrafficDirection.NS
							&& list2.getTrafficDirection() == TrafficDirection.WE) {
						((Car) o).setCurrentlane(TrafficLane.LEFT); // turn left
						carsLeft.add(o);
						//((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
					} else if (list1.getTrafficDirection() == TrafficDirection.SN
							&& list2.getTrafficDirection() == TrafficDirection.WE) {
						((Car) o).setCurrentlane(TrafficLane.RIGHT); // turn right
						carsRight.add(o);
						//((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
					} else if (list1.getTrafficDirection() == TrafficDirection.SN
							&& list2.getTrafficDirection() == TrafficDirection.EW) {
						((Car) o).setCurrentlane(TrafficLane.LEFT); // turn left
						carsLeft.add(o);
						//((Car) o).setPositioninQueue(currentl, ((Car) o).getCurrentlane());
					}  
				     
			  }
			  
		}
		if (((Car) o).getCurrentlane() == TrafficLane.LEFT)
			((Car) o).setPositionInQueue(carsLeft.size());
		else if (((Car) o).getCurrentlane() == TrafficLane.MIDDLE)
			((Car) o).setPositionInQueue(carsMid.size());
		else if (((Car) o).getCurrentlane() == TrafficLane.RIGHT)
			((Car) o).setPositionInQueue(carsRight.size());
		((Car) o).addEvent();
	
	
		if (TRAFFIC_LIGHT_DEBUG) {
		
			 System.out.println((Car) o + " has entered Intersection " + intersection + " " + this + " "
					+ ((Car) o).getCurrentlane() + " lane " + " in position: " + ((Car) o).getPositionInQueue());

		}	
	
		}
	

	@Override
	public void removeObserverCar(ObserverCar o) {
		
		switch(((Car) o).getCurrentlane()){
		case LEFT:
			carsLeft.remove(o);
			break;
		case MIDDLE:
			carsRight.remove(o);
			break;
		case RIGHT:
			carsMid.remove(o);
			break;
		default:
			System.out.println("CAN'T FIND " + o + "IN A LANE");
			break;
		}
		
		TrafficLight nextLight = ((Car) o).getNextTrafficLight();
 
		if (nextLight != null) {
			// Assign the car to the next light in the path
			TrafficLight l = ((Car) o).getCurrentLight();
			
			((Car) o).moveToNextLight();
			if (TRAFFIC_LIGHT_DEBUG) {
				System.out.println((Car) o + " has left Intersection " + intersection + " " + this);
			}
		} else {
			// We have fully gone through the path and it is time for the car to
			// leave
			//System.out.println((Car) o + " has Last light "  + this);
			o.removeFromGrid();
			if (TRAFFIC_LIGHT_DEBUG) {
				System.out.println((Car) o + " has left Grid at " + intersection + " " + this);
			}
		}
	}

	@Override
	public void notifyAllObserverCar() {
		// TODO Auto-generated method stub

	}

	public ArrayList<ObserverCar> getCarsLeft() {
		return carsLeft;
	}

	public void setCarsLeft(ArrayList<ObserverCar> carsLeft) {
		this.carsLeft = carsLeft;
	}

	public ArrayList<ObserverCar> getCarsMid() {
		return carsMid;
	}

	public void setCarsMid(ArrayList<ObserverCar> carsMid) {
		this.carsMid = carsMid;
	}

	public ArrayList<ObserverCar> getCarsRight() {
		return carsRight;
	}

	public void setCarsRight(ArrayList<ObserverCar> carsRight) {
		this.carsRight = carsRight;
	}

	// return car number in each lane, 0-> left, 1-> mid, 2 -> right
	public int[] getNumCars() {
		int[] cars = new int[3];
		cars[0] = carsLeft.size();
		cars[1] = carsMid.size();
		cars[2] = carsRight.size();
		return cars;
	}
	
	public int findPosition(Car c){
		int position = 0;
		switch(c.getCurrentlane()){
		case LEFT:
			position = carsLeft.indexOf(c);
			break;
		case MIDDLE:
			position = carsMid.indexOf(c);
			break;
		case RIGHT:
			position = carsRight.indexOf(c);
			break;
		default:
			break;
		}
		return position ;
	}

	public String toString() {
		return lightNumber + ": " + lightStatus + "(" + lightTime + ")";
	}
	public int checkLaneLimitReached(){
		if ( carsLeft.size() > laneLimit || carsMid.size() > laneLimit || carsRight.size() > laneLimit ) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public void getPublication(String arg) {
		// TODO Auto-generated method stub
		if(TRAFFIC_LIGHT_DEBUG)
			System.out.println("Got: " + arg);
	}
	
	
}