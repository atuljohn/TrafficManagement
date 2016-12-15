package com.cen6087.management;

import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;

import com.cen6087.models.Car;
import com.cen6087.models.CarEvent;
import com.cen6087.models.Event;
import com.cen6087.models.Grid;
import com.cen6087.models.Intersection;
import com.cen6087.models.TrafficLight;
import com.cen6087.models.TrafficLightEvent;
import com.cen6087.models.TrafficManagementStatic;
import com.cen6087.models.Event.EventTypeEnum;
import com.cen6087.models.TrafficLight.LightColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.cen6087.management.TrafficManagement;

public class Statistic {
	
	public static boolean STATISTIC_DEBUG = false;
	public static boolean FINAL_REPORT = false;
	private ArrayList<Integer> stat = new ArrayList<Integer>();
	private ArrayList<String> printVal = new ArrayList<String>();
	private ArrayList<String> printGrid = new ArrayList<String>();
	private ArrayList<String> printAlgoVal = new ArrayList<String>();
	private ArrayList<String> printRateVal = new ArrayList<String>();	
	private ArrayList<String> printInitalVal = new ArrayList<String>();	
	private ArrayList<TrafficLight> carPath = new ArrayList<TrafficLight>();
	private ArrayList<String> path = new ArrayList<String>();
	private ArrayList<String> light = new ArrayList<String>();
	
	
	private int algoUsed;
	private int nCars = 0;
	private int rCars = 0;
	int a = 0;
	int b = 0;
	String t = "trigger";
	String n = "newLine";
	int carExited = 0;
	private ArrayList<Float> exitRate = new ArrayList<Float>();
	private ArrayList<Float> arrivalRate = new ArrayList<Float>();
	private ArrayList<Integer> arrivalTimes = new ArrayList<Integer>();
	private ArrayList<Integer> exitTimes = new ArrayList<Integer>();
	private ArrayList<Float> waitingTime = new ArrayList<Float>();
	//float waitingTime = 0;
	int startTime = 0;
	int finalTime = 0;
	float avgArrivalTime = 0;
	float avgExitTime = 0;
	float avgWaitingTime = 0;
	float avgArrivalRate = 0;
	float avgExitRate = 0;
	int i = 0;
	
	public void carArrivalTime(Car c){
		nCars++;
		if (STATISTIC_DEBUG) System.out.println("==== # Cars === "+nCars);
		startTime = c.getArrivalTime();
		startTime = (startTime == 0) ? 1 : startTime;
		if (STATISTIC_DEBUG) System.out.println("==== StartTime === "+startTime);
		arrivalRate.add((float)nCars/(float)startTime);
		arrivalTimes.add(startTime);
		if (STATISTIC_DEBUG) System.out.println("ArrivalRate "+arrivalRate);
	}
	
	public void carStatCarTime(Car c){
		ArrayList<String> pathTemp = new ArrayList<String>();
		ArrayList<String> lightTemp = new ArrayList<String>();
		if (STATISTIC_DEBUG) System.out.println("CurrentLight"+c.getCurrentLight());
			stat.add(c.getId());
			if (STATISTIC_DEBUG) 	System.out.println("CarID "+c.getId());
			stat.add(c.getArrivalTime());
			if (STATISTIC_DEBUG) 	System.out.println("CarArrivalTime "+c.getArrivalTime());
			exitTimes.add(c.getExitTime());
			stat.add(c.getExitTime());
			waitingTime.add(c.getWaitingTime());
			if (STATISTIC_DEBUG) System.out.println("CarExitTime "+c.getExitTime());
			carPath = c.getPath();
			if (STATISTIC_DEBUG) System.out.println("CarPath "+c.getPath());
			for (TrafficLight l : carPath) {
				lightTemp.add(l.getLightNumber().toString()+": "+l.getLightStatus()+"("+l.getLightTime()+")");
				if (STATISTIC_DEBUG) System.out.println("CarLights"+l.getLightNumber().toString()+" "+l.getLightStatus()+" "+l.getLightTime());
				pathTemp.add(l.getIntersection().toString());
				if (STATISTIC_DEBUG) System.out.println("CarIntersections"+l.getIntersection().toString());
			}
			light.add(lightTemp.toString());
			if (STATISTIC_DEBUG) System.out.println("===Car Light==="+light);
			path.add(pathTemp.toString());
			if (STATISTIC_DEBUG) System.out.println("===Car Path==="+path);
			carExited++;
			if (STATISTIC_DEBUG) System.out.println("CarsExited"+carExited);
			finalTime = c.getExitTime();
			if (STATISTIC_DEBUG) System.out.println();
			//==========================================================
			exitRate.add((float)carExited/(float)finalTime);
			if (STATISTIC_DEBUG) System.out.println("CarsExited"+carExited+"Final Time"+finalTime);
			if (STATISTIC_DEBUG) System.out.println("ExitRate "+exitRate);
	}
	
	public void carStats(){
		int j = 0;
		int s = 0;
		printInitalVal.add(t);
		printInitalVal.add("Total Cars in grid: "+nCars); 
		if (STATISTIC_DEBUG) System.out.println("Total Cars in grid: "+nCars);
		printInitalVal.add(n);
		if(stat.isEmpty()){
				System.out.println("Cars not exited the grid");
			printVal.add("No car exited the grid");
		}else{			
			printInitalVal.add(t);
			printInitalVal.add("Cars moved out of Grid "+carExited);
		if (STATISTIC_DEBUG) System.out.println("Cars moved out of Grid");
		printInitalVal.add(n);
		if (FINAL_REPORT){
			for(int i=0; i<stat.size(); i+=3){
				printInitalVal.add("Car ID: "+stat.get(i));
				if (STATISTIC_DEBUG) System.out.println("Car ID: "+stat.get(i));
				printInitalVal.add(n);
				}
			}				
		for(int i=0; i<stat.size(); i++){
			if(i%3==0){
				printVal.add(t);
				printVal.add("Car ID: "+stat.get(i));
				if (STATISTIC_DEBUG) System.out.println("Car ID: "+stat.get(i));
				printVal.add(n);
				printVal.add("Path Traversed: "+path.get(j));
				if (STATISTIC_DEBUG) System.out.println("Path Traversed: "+path.get(j));
				printVal.add(n);
				printVal.add("Lights Encountered: "+light.get(j));
				if (STATISTIC_DEBUG) System.out.println("Lights Encountered: "+light.get(j));
				printVal.add(n);	
				j++;
				}else if(i%3==1){ 
					a += stat.get(i);
					printVal.add("Arrival Time: "+stat.get(i));
					if (STATISTIC_DEBUG) System.out.println("Arrival Time: "+stat.get(i));
					printVal.add(n);
				}else if(i%3==2){ 
					b += stat.get(i);
					printVal.add("Exit Time: "+stat.get(i));
					if (STATISTIC_DEBUG) System.out.println("Exit Time: "+stat.get(i));
					printVal.add(n);
				}			
			}
		printVal.add(t);
		printVal.add("Total Time: "+(b-a));
		if (STATISTIC_DEBUG) System.out.println("Total Time: "+(b-a));
		printVal.add(n);
		printVal.add("Average Traversal Time: "+(float)(b-a)/(float)j);
		if (STATISTIC_DEBUG) System.out.println("Average Traversal Time: "+(b-a)/j);
		printVal.add(n);
		}
	}
	
	public void rateStats(){
		if (FINAL_REPORT) {
			printRateVal.add(t);
			printRateVal.add("Arrival Time of Cars");
			printRateVal.add(n);
		}
		Collections.sort(arrivalTimes);
		for(int i=0;i<arrivalTimes.size();i++){
			if (STATISTIC_DEBUG) System.out.println(" "+arrivalTimes.get(i));
			avgArrivalTime += arrivalTimes.get(i);
			if (FINAL_REPORT) {
				printRateVal.add(""+arrivalTimes.get(i));
				printRateVal.add(n);
			}			
		}		
		printRateVal.add(t);
		avgArrivalTime = avgArrivalTime/arrivalTimes.size();
		printRateVal.add("Average Arrival Time: "+avgArrivalTime);		
		printRateVal.add(n);
		
		if (FINAL_REPORT){
			printRateVal.add(t);	
			printRateVal.add("Exit Time of Cars");
			printRateVal.add(n);
		}
		for(int i=0;i<exitTimes.size();i++){
			if (STATISTIC_DEBUG) System.out.println(" "+exitTimes.get(i));		
			avgExitTime += exitTimes.get(i);
			if (FINAL_REPORT){
				printRateVal.add(""+exitTimes.get(i));
				printRateVal.add(n);
			}
		}
		printRateVal.add(t);
		avgExitTime = avgExitTime/exitTimes.size();
		printRateVal.add("Average Exit Time: "+avgExitTime);		
		printRateVal.add(n);
		if (FINAL_REPORT){
			printRateVal.add(t);	
			printRateVal.add("Wating Time of Cars");
			printRateVal.add(n);
		}		
		for(int i=0;i<waitingTime.size();i++){
			if (STATISTIC_DEBUG) System.out.println(" "+waitingTime.get(i));	
			avgWaitingTime += waitingTime.get(i);
			if (FINAL_REPORT){
				printRateVal.add(""+waitingTime.get(i));
				printRateVal.add(n);
			}			
		}
		printRateVal.add(t);
		avgWaitingTime = avgWaitingTime/waitingTime.size();
		printRateVal.add("Average Waiting Time: "+avgWaitingTime);		
		printRateVal.add(n);
		
		if (FINAL_REPORT){
			printRateVal.add(t);
			printRateVal.add("Arrival Rate of Cars");
			printRateVal.add(n);
		}		
		for(int i=0;i<arrivalRate.size();i++){
			if (STATISTIC_DEBUG) System.out.println(i+". ArrivalRate "+arrivalRate.get(i));	
			avgArrivalRate += arrivalRate.get(i);
			if (FINAL_REPORT){
				printRateVal.add(""+arrivalRate.get(i));
				printRateVal.add(n);
			}
			
		}
		printRateVal.add(t);
		avgArrivalRate = avgArrivalRate/arrivalRate.size();
		printRateVal.add("Average Arrival Rate: "+avgArrivalRate);		
		printRateVal.add(n);
		if (FINAL_REPORT){
			printRateVal.add(t);
			printRateVal.add("Exit Rate of Cars");
			printRateVal.add(n);
		}		
		for(int i=0;i<exitRate.size();i++){
			if (STATISTIC_DEBUG) System.out.println(i+". ExitRate "+exitRate.get(i));
			avgExitRate += exitRate.get(i);
			if (FINAL_REPORT){
				printRateVal.add(""+exitRate.get(i));
				printRateVal.add(n);
			}
			
		}

		waitingTime.toString();
		printRateVal.add(t);
		avgExitRate = avgExitRate/exitRate.size();
		printRateVal.add("Average Exit Rate: "+avgExitRate);
		printRateVal.add(n);
	}
	
	public void gridStats(Grid g){
		int ave = g.getTotalAvenues();
		int st = g.getTotalStreets();
		printGrid.add("==================== GRID MAP =========================");
		if (STATISTIC_DEBUG) System.out.println("==================== GRID MAP =========================");		
		printGrid.add(n);
		printGrid.add("Avenues "+ave+"\t Streets "+st);
		if (STATISTIC_DEBUG) System.out.println("Avenues "+ave+"\t Streets "+st);
		printGrid.add(n);
		for(int i=0; i<ave; i++){
			for(int j=0; j<st; j++){			
				if (STATISTIC_DEBUG) System.out.print(g.getIntersection(i,j).toString()+"\t");				
				printGrid.add(g.getIntersection(i,j).toString()+"\t");
			}
			printGrid.add(n);
			if (STATISTIC_DEBUG) System.out.println("");
		}		
	}
	
	public void printAlgorithmUsed(int algoUsed) {
		this.algoUsed = algoUsed;
		printAlgoVal.add(t);
		printAlgoVal.add("Algorithm Used");
		printAlgoVal.add(n);
		switch(algoUsed) {
		case 1:
			printAlgoVal.add("Using algorithm 1");
			break;
		case 2:
			printAlgoVal.add("Using algorithm 2: Lights change when queue threshold is reached");
			break;
		case 3:
			printAlgoVal.add("Using algorithm 3: Lights talk to each other");
			break;
		}

		printAlgoVal.add(n);
	}
	
	public void print(ArrayList<String> printValue, PrintWriter writer){
		for(int i=0; i<printValue.size();i++){
			if(printValue.get(i)==t){
				writer.println("=======================================================");
			}else if(printValue.get(i)==n){
				writer.println("");
			}else if(printValue.get(i)==n){
				writer.println("");
			}else{
				writer.print(printValue.get(i));
			}
		}
	}
	
	public void printf(ArrayList<Float> printVal, PrintWriter writer){
		for(int i=0; i<printVal.size();i++){
		
				writer.print(printVal.get(i));
			
		}
	}
		
	
	public void writeToFile(){
		if (STATISTIC_DEBUG) System.out.println("Wite to file");
		try{
			Date today = new Date();
			String filename = new String();
			Format formatter = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
			filename = ("LogEntry "+formatter.format(today)+".txt");
			PrintWriter w = new PrintWriter(filename, "UTF-8");
		
			if (FINAL_REPORT) print(printGrid, w);
			if (FINAL_REPORT) print(printAlgoVal,w);
		    carStats();
		    print(printInitalVal, w);
		    print(printVal, w);
		    rateStats();
		    print(printRateVal, w);

		    w.close();
		    if (STATISTIC_DEBUG) System.out.println("Done");
		    
		}catch (Exception e){
			if(printVal.isEmpty()){
				System.out.println("Print_val is empty");
			}
				System.out.println("Exception Invoked");
		}

	}


	
}