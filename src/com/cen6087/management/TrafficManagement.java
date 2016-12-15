package com.cen6087.management;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cen6087.models.Car;
import com.cen6087.models.CarEvent;
import com.cen6087.models.Event;
import com.cen6087.models.Grid;
import com.cen6087.models.Intersection;
import com.cen6087.models.SubscriberLight;
import com.cen6087.models.TrafficLight;
import com.cen6087.models.TrafficLightEvent;
import com.cen6087.models.TrafficManagementStatic;
import com.cen6087.models.Event.EventTypeEnum;
import com.cen6087.models.TrafficLight.LightColor;

/*
 * This will generate and test the cases in future.
 * 
 */
public class TrafficManagement {

	private static final boolean PRINT_TIME = false;
	public static boolean TRAFFIC_MANAGEMENT_DEBUG = false;
	public static boolean PRINT_OUTPUT = false;
	// public static double lamda=2;
	public int idd;

	// Used to select which algorithm to be used.
	// 1 = Dumb algorithm,
	// 2 = Change lights based on queue threshold.
	// 3 = pass information to adjacent 2 lights.
	private int algoSelect = 1;

	// Memo array to check if an intersection has already been processed
	private boolean[][] intersectionMemo;

	private int simulationTime = 5000;
	private long simulationStartTime;
	private long simulationCompletionTime;

	private Grid grid;
	private ArrayList<Intersection> arrivalIntersectionList = new ArrayList<Intersection>();
	private EventList eventList;

	private Statistic stat = new Statistic();

	private ArrayList<Car> carList = new ArrayList<Car>();

	private int totalAvenue;
	private int totalStreet;
	private float distanceBetweenIntersection;
	private int totalCars;
	private int speed;
	private float acceleration;
	private int carSize; // and room between cars
	private int roomBetweenCar;
	private int convoySize;

	// holds the value of how long the simulator has been running
	private int currentTime;

	public TrafficManagement(int algoSelect) {
		// TODO Auto-generated constructor stub
		this.algoSelect = algoSelect;
	}

	public int getSimulationTime() {
		return this.simulationTime;
	}

	public int setSimulationTime(int simulationTime) {
		return this.simulationTime = simulationTime;
	}

	public void readConfigFile() {
		Pattern semicolon = Pattern.compile(";");
		Pattern colon = Pattern.compile(":");
		Pattern number = Pattern.compile("([0-9]+)");

		Matcher infoLine;
		Matcher findValue;
		Matcher infoNumber;

		try {
			BufferedReader br = new BufferedReader(new FileReader("TrafficManage/src/Config"));
			try {
				String line;
				while ((line = br.readLine()) != null) {

					infoLine = semicolon.matcher(line);
					if (infoLine.find()) {
						String infoLineString = line.substring(0, infoLine.start());
						findValue = colon.matcher(infoLineString);
						if (findValue.find()) {
							String infoType = infoLineString.substring(0, findValue.start());
							String info = infoLineString.substring(findValue.end());
							infoNumber = number.matcher(info);
							if (infoNumber.find()) {
								if (infoType.equals(TrafficManagementStatic.TOTAL_AVENUE_STRING)) {
									totalAvenue = Integer.parseInt(infoNumber.group(0));
								} else if (infoType.equals(TrafficManagementStatic.TOTAL_STREET_STRING)) {
									totalStreet = Integer.parseInt(infoNumber.group(0));
								} else if (infoType
										.equals(TrafficManagementStatic.DISTANCE_BETWEEN_INTERSECTION_STRING)) {
									setDistanceBetweenIntersection(Integer.parseInt(infoNumber.group(0)));
								} else if (infoType.equals(TrafficManagementStatic.TOTAL_CAR_STRING)) {
									totalCars = Integer.parseInt(infoNumber.group(0));
								} else if (infoType.equals(TrafficManagementStatic.SPEED_STRING)) {
									speed = Integer.parseInt(infoNumber.group(0));
								} else if (infoType.equals(TrafficManagementStatic.CAR_SIZE_STRING)) {
									carSize = Integer.parseInt(infoNumber.group(0));
								} else if (infoType.equals(TrafficManagementStatic.ROOM_BETWEEN_CAR_STRING)) {
									roomBetweenCar = Integer.parseInt(infoNumber.group(0));
								} else if (infoType.equals(TrafficManagementStatic.YELLOW_TIME_STRING)) {
									TrafficManagementStatic.yellowTime = Integer.parseInt(infoNumber.group(0));
								} else if (infoType.equals(TrafficManagementStatic.LANE_LIMIT_STRING)) {
									TrafficManagementStatic.laneLimit = Integer.parseInt(infoNumber.group(0));
									convoySize = Math.round(TrafficManagementStatic.laneLimit * 0.7f);
								}
							}
						}

					}

				}
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void generateGrid() {
		grid = new Grid(totalAvenue, totalStreet);
		grid.createGrid();

		arrivalIntersectionList = grid.arrivalIntersectionList();
		stat.gridStats(grid);

	}

	// TODO Make it generate Random number of cars.
	// Currently generates only one.
	/*
	 * public static int getPoisson(double lambda) { double L =
	 * Math.exp(-lambda); double p = 1.0; int k = 0;
	 * 
	 * do { k++; p *= Math.random(); } while (p > L);
	 * 
	 * return k - 1; }
	 */
	public void generateCars() {
		idd++;
		// int carCount = new Random().nextInt(10);

		// for (int i = 0; i < totalCars; i++) {
		ArrayList<TrafficLight> path = grid.lightpathfinder(arrivalIntersectionList);
		if (TRAFFIC_MANAGEMENT_DEBUG) {
			System.out.print("path: ");
			for (TrafficLight l : path) {
				System.out.print(l.getIntersection() + " " + l + " ");
			} // }
			System.out.println();
		}

		// // }}

		// int timestamp = currentTime + getPoisson(2);
		// System.out.println("The time stamp for car" + timestamp);

		Car car = new Car(idd, currentTime, roomBetweenCar, carSize, path, this);
		carList.add(car);
		// if(currentTime != timestamp)
		// {
		path.get(0).attachObserverCar(car);
		if (TRAFFIC_MANAGEMENT_DEBUG) {
			System.out.println(idd + "car attach");
		}
		// }

		stat.carArrivalTime(car);
	}

	// removes the car object from the list, gets called when the car is leaving
	// grid
	public void removeCar(Car c) {
		stat.carStatCarTime(c);
		// System.out.println(c + " out of the system at " +
		// this.getCurentTime());
		carList.remove(c);
	}

	public void generateEvent(int flag, double lamda) {
		if (TRAFFIC_MANAGEMENT_DEBUG) {
			System.out.println("Generating Events");
		}
		eventList = new EventList(this);
		simulationTime = eventList.generateCarEvent(simulationTime, totalCars, flag, lamda, totalAvenue, totalStreet);
	}

	public void beginSimulation(int timePeriod) {
		// eventList.executeEvent();
		simulationStartTime = System.currentTimeMillis();
		if (TRAFFIC_MANAGEMENT_DEBUG) {
			System.out.println("Simulation Begin");
		}
		intersectionMemo = new boolean[totalAvenue][totalStreet];
		stat.printAlgorithmUsed(algoSelect);

		// Publisher Subscriber stuff

		// Find every Traffic Light's subscriber.
		for (int a = 0; a < totalAvenue; a++) {
			for (int s = 0; s < totalStreet; s++) {
				ArrayList<Intersection> adjList = grid.findAdjIntersections(grid.getIntersection(a, s));
				TrafficLight temp1 = grid.getIntersection(a, s).getLight1();
				TrafficLight temp2 = grid.getIntersection(a, s).getLight2();

				for (int i = 0; i < adjList.size(); i++) {
					int[] adjLightIndex = adjList.get(i).getIndex();
					// When the avenue number of the current intersection and of
					// the subscriber are different,
					// the subscribers light1 will be added.
					// eg: if current intersection is [0,0], light2 of [0,1] and
					// light 1 of [1,0]
					// will be added as subscribers to light1 and light2
					// we add the subscriber to both light1 and light2 of the
					// current intersection.
					if (a != adjLightIndex[0]) {
						temp1.subscribe(adjList.get(i).getLight1());
						temp2.subscribe(adjList.get(i).getLight1());
					} else if (s != adjLightIndex[1]) {
						temp1.subscribe(adjList.get(i).getLight2());
						temp2.subscribe(adjList.get(i).getLight2());
					}
				}
			}
		}

		for (int time = 0; time < timePeriod; time++) {
			currentTime = time;
			if (TRAFFIC_MANAGEMENT_DEBUG || PRINT_TIME) {
				System.out.println("Time: " + time);
			}

			ArrayList<Event> list = eventList.getFromEventList(time) != null
					? new ArrayList<Event>(eventList.getFromEventList(time)) : null;

			if (list != null) {
				for (Event event : list) {

					if (event.getEventType() == Event.EventTypeEnum.CAR) {
						generateCars();
					}
					if (event.getEventType() == Event.EventTypeEnum.INTERSECTION) {
						// CarEvent e = (CarEvent) event;
						// e.getCar().processEvent();

					} else if (event.getEventType() == Event.EventTypeEnum.CARTOINTERSECTION) {
						CarEvent e = (CarEvent) event;
						e.getCar().processEvent(event);
					}

					else if (event.getEventType() == Event.EventTypeEnum.CARTOCAR) {
						CarEvent e = (CarEvent) event;
						e.getCar().processEvent(event);
					}

					if (event.getEventType() == EventTypeEnum.TRAFFICLIGHT) {
						// This is where we handle the Event sent by the
						// subscribers.
						TrafficLightEvent e = (TrafficLightEvent) event;
						TrafficLight light = e.getTrafficLight();
						Intersection tempIntersection = light.getIntersection();
						if (TRAFFIC_MANAGEMENT_DEBUG) {
							System.out.println("inside TrafficLight event");
						}

						int[] index = light.getIntersection().getIndex();
						if (TRAFFIC_MANAGEMENT_DEBUG) {
							System.out.println("\n\n A FORCED CHANGE WILL HAPPEN HERE for intersection" + index[0] + ","
									+ index[1] + " for " + light.getLightNumber() + "!!!\n\n\n");
						}
						TrafficLight actualTrafficLight = null;
						TrafficLight lightPair = null;

						// Handling switching here.
						if (light.getLightNumber().equals("light1")) {
							actualTrafficLight = grid.getIntersection(index[0], index[1]).getLight1();
							lightPair = grid.getIntersection(index[0], index[1]).getLight2();
						} else if (light.getLightNumber().equals("light2")) {
							actualTrafficLight = grid.getIntersection(index[0], index[1]).getLight2();
							lightPair = grid.getIntersection(index[0], index[1]).getLight1();
						}
						// Three cases:
						// 1. if lightPair is green, turn to yellow
						// 2. if lightPair is yellow, do nothing
						// 3. if actualTrafficLight is green, reset timer
						// 4. if actualTrafficLight is yellow, turn green
						if (lightPair.getLightStatus() == LightColor.GREEN) {
							lightPair.setLightStatus(LightColor.YELLOW);
							intersectionMemo[index[0]][index[1]] = true;
						} else if (actualTrafficLight.getLightStatus() == LightColor.GREEN) {
							// TODO
							// improve to check if light had just turned green.
							actualTrafficLight.setLightStatus(LightColor.GREEN);
							intersectionMemo[index[0]][index[1]] = true;
						} else if (actualTrafficLight.getLightStatus() == LightColor.YELLOW) {
							actualTrafficLight.setLightStatus(LightColor.GREEN);
							intersectionMemo[index[0]][index[1]] = true;
						}

					}
				}
			} else {
				if (TRAFFIC_MANAGEMENT_DEBUG) {
					// System.out.println("No event for this second!");
				}
			}

			if (time != 0) {
				updateTrafficLights(algoSelect, intersectionMemo);
			}
			// printing out the status of all lights
			// printSnapshot();
		}
	}

	public void printSnapshot() {
		for (int a = 0; a < totalAvenue; a++) {
			for (int s = 0; s < totalStreet; s++) {
				System.out.print("[" + a + "]" + "[" + s + "] " + grid.getIntersection(a, s).getLight1() + ", "
						+ grid.getIntersection(a, s).getLight2() + "\t");
			}
			System.out.println();
		}

	}

	public void updateTrafficLights(int algoSelect, boolean[][] intersectionMemo) {
		TrafficLight temp1, temp2, tempActive;
		// Bug fix, checks to prevent light turning yellow
		// and decrementing value same time
		boolean justTurnedYellowFlag;

		this.intersectionMemo = intersectionMemo;
		switch (algoSelect) {
		case 1:
			// no need to check for memo
			intersectionMemo = new boolean[totalAvenue][totalStreet];
			break;
		case 2:
		case 3:
			// print out memo values simply
			for (int a = 0; a < totalAvenue; a++) {
				for (int s = 0; s < totalStreet; s++) {
					if (TRAFFIC_MANAGEMENT_DEBUG)
						System.out.println(a + " " + s + " :" + intersectionMemo[a][s] + " ");
					// This will contain the list of events which will be
					// returned by publish.
					ArrayList<Event> list = new ArrayList<Event>();
					// check if the inactive light has too many cars, then
					// switch the active lights
					temp1 = grid.getIntersection(a, s).getLight1();
					temp2 = grid.getIntersection(a, s).getLight2();
					tempActive = grid.getIntersection(a, s).getActiveLight();
					// check which light is active and check the queue status of
					// the other light
					if (tempActive.getLightNumber() == "light1") {
						// System.out.print(tempActive.getLightNumber() + " is
						// active");
						if (temp2.checkLaneLimitReached() == 1) {
							if (temp1.getLightStatus() == LightColor.GREEN) {
								if (TRAFFIC_MANAGEMENT_DEBUG) {
									System.out.println(" Lane limit reached in light2!");
								}
								temp1.setLightStatus(LightColor.YELLOW);
								intersectionMemo[a][s] = true;
								if (algoSelect == 3) {
									list = temp2
											.publish("light 2 in" + a + "," + s + "will change in next time instant");
								}

							}

						} else {
							// System.out.print(" Lane limit not reached yo!");
						}
					} else {
						// System.out.print(tempActive.getLightNumber() + " is
						// active");
						if (temp1.checkLaneLimitReached() == 1) {
							if (temp2.getLightStatus() == LightColor.GREEN) {
								if (TRAFFIC_MANAGEMENT_DEBUG) {
									System.out.println(" Lane limit reached in light1!");
								}
								temp2.setLightStatus(LightColor.YELLOW);
								intersectionMemo[a][s] = true;
								if (algoSelect == 3) {
									list = temp1
											.publish("light 1 in" + a + "," + s + "will change in next time instant");
								}
							}
						} else {
							// System.out.print(" Lane limit not reached yo!");
						}
					}
					try {
						if (list.size() != 0) {
							if (TRAFFIC_MANAGEMENT_DEBUG) {
								System.out.println("our list size is:" + list.size());
							}
							// System.out.println("the eventList size before
							// is:" +
							// eventList.getEventListSize(currentTime+3));
							eventList.addToEventList(currentTime + 1, list);
							if (TRAFFIC_MANAGEMENT_DEBUG) {
								System.out.println(
										"the eventList size after is:" + eventList.getEventListSize(currentTime + 1));
							}
						}

					} catch (NullPointerException e) {

					}
				}
				// System.out.println();
				if (TRAFFIC_MANAGEMENT_DEBUG) {
					System.out.println();
				}
			}
			break;
		case 4:
			for (int i = 0; i < totalAvenue; i++) {
				for (int j = 0; j < totalStreet; j++) {
					TrafficLight light1, light2;
					light1 = grid.getIntersection(i, j).getLight1();
					light2 = grid.getIntersection(i, j).getLight2();

					temp1 = grid.getIntersection(i, j).getActiveLight().getLightNumber().equals(light1) ? light1
							: light2;
					temp2 = temp1.equals(light1) ? light2 : light1;

					if (temp1.numM > convoySize) {
						// System.out.println("Still have a convoy at
						// intersection: " + grid.getIntersection(i, j) + ", "
						// + temp1.getLightNumber());
						temp1.setGreenTime(temp1.numM);
						temp1.setLightStatus(LightColor.GREEN);
						temp2.setRedTime(temp1.getGreenYellowTime());
						temp2.setLightStatus(LightColor.RED);
					}
					if (temp1.numM < convoySize && temp2.numM > convoySize) {
						// System.out.println("Other light has a convoy!");
						temp1.setLightStatus(LightColor.YELLOW);
					}
				}
			}
			break;
		}
		for (int a = 0; a < totalAvenue; a++) {
			for (int s = 0; s < totalStreet; s++) {
				// if lane limit is reached, the memo value will be true
				// and we skip updating the light because it just turned to
				// yellow
				if (intersectionMemo[a][s] == false) {
					temp1 = grid.getIntersection(a, s).getLight1();
					temp2 = grid.getIntersection(a, s).getLight2();
					justTurnedYellowFlag = false;

					// Handling switching from GREEN to YELLOW
					if (temp1.getLightStatus() == LightColor.GREEN) {
						if (!temp1.decreaseLightTime()) {
							temp1.setLightStatus(LightColor.YELLOW);
							justTurnedYellowFlag = true;
						}
					} else if (temp2.getLightStatus() == LightColor.GREEN) {
						if (!temp2.decreaseLightTime()) {
							temp2.setLightStatus(LightColor.YELLOW);
							justTurnedYellowFlag = true;
						}
					}
					// Handling switching from YELLOW to RED
					if (!justTurnedYellowFlag && temp1.getLightStatus() == LightColor.YELLOW) {
						if (!temp1.decreaseLightTime()) {
							grid.getIntersection(a, s).swapActiveLight();
						}
					} else if (!justTurnedYellowFlag && temp2.getLightStatus() == LightColor.YELLOW) {
						if (!temp2.decreaseLightTime()) {
							grid.getIntersection(a, s).swapActiveLight();
						}
					}
				} else {
					// since memo is true, we skip updating the lights and
					// revert the value of memo to false
					intersectionMemo[a][s] = false;
					// We also notify the subscriber lights if we are using
					// Algorithm 3.
					if (algoSelect == 3) {

					}
				}
			}
		}
	}

	/*
	 * This should give the status of overall Grid, Cars and Eventlist (if
	 * possible) For now, it just prints the time it took to finish execution.
	 */
	public void takeSnapshot() {
		long diff;
		simulationCompletionTime = System.currentTimeMillis();
		diff = simulationCompletionTime - simulationStartTime;
		System.out.println("Time completed: " + diff / 1000);
	}

	public void showStat() {
		stat.writeToFile();
	}

	public EventList getEventList() {
		return eventList;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		TrafficManagement tm;

		System.out.println("1. Dumb scheduling");
		System.out.println("2. Self-managed system");
		System.out.println("3. Coordinated scheduling");
		System.out.println("4. Convoy");
		System.out.print("Choose the algorithm to execute: ");
		Scanner in = new Scanner(System.in);

		switch (in.nextInt()) {
		case 1:
			System.out.println("Dumb scheduling is being executed");
			tm = new TrafficManagement(1);
			break;
		case 2:
			System.out.println("Self-managed system is being executed");
			tm = new TrafficManagement(2);
			break;
		case 3:
			System.out.println("Coordinated scheduling is being executed");
			tm = new TrafficManagement(3);
			break;
		case 4:
			System.out.println("Convoy is being executed");
			tm = new TrafficManagement(4);
			break;
		default:
			System.out.println("Wrong input!");
			System.out.println("Dumb scheduling is being executed by default");
			tm = new TrafficManagement(1);
			break;
		}

		// TrafficManagement tm = new TrafficManagement();

		tm.setSimulationTime(5000);
		tm.readConfigFile();
		tm.generateGrid();
		tm.generateEvent(1, 0.05);
		System.out.println("Simulation Time: " + tm.getSimulationTime());
		tm.beginSimulation(tm.getSimulationTime());
		tm.showStat();
		System.out.println("Cars still in the system count: " + tm.carList.size());

		// for (Car item : tm.carList)
		// System.out.println("the car which still stays in the system is " +
		// item);

	}

	public int getCurentTime() {
		// TODO Auto-generated method stub
		return currentTime;
	}

	public float getDistanceBetweenIntersection() {
		return distanceBetweenIntersection;
	}

	public void setDistanceBetweenIntersection(float distanceBetweenIntersection) {
		this.distanceBetweenIntersection = distanceBetweenIntersection;
	}

}