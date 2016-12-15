// This module will contain the instantiations of the various objects
// EventList will be responsible for creating the ArrayList of events,
// such as car generation, traffic light toggling and so on

// handled by atul

//

package com.cen6087.management;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.cen6087.models.Event;

public class EventList {

	public static boolean EVENTLIST_DEBUG = false;
	private TrafficManagement tm;
	private ConcurrentHashMap<Long, ArrayList<Event>> eList = new ConcurrentHashMap<Long, ArrayList<Event>>();

	// Time for the entire simulation
	private int timePeriod;

	public EventList(TrafficManagement tm) {
		super();
		this.tm = tm;
	}

	/*
	 * This function generates car generation / traffic light set/change event
	 * before simulation begins. using an ArrayList<Event> to store the Events.
	 */
	public int generateCarEvent(int timePeriod, int totalcar, int dist_flag, double lamda,int totalAvenue, int totalStreet) {
		this.timePeriod = timePeriod;

		if (dist_flag == 0) {
			// Only allow for one car to be generated
			ArrayList<Event> list = new ArrayList<Event>();
			list.add(new Event(Event.EventTypeEnum.CAR));
			addToEventList(0, list);
		}

		else if (dist_flag == 1) {
			int time;
			int over=0;
			int entry = totalAvenue + totalStreet;
			if( totalcar % entry == 0 ) {
			    for (int event = 0; event < (totalcar/entry); event++) 
			    	   { time = 0;
			             for (int e=0; e < entry;e++)
			              {  Random r = new Random();
			                 int temp =  (int) (-Math.log(1 - r.nextFloat()) / lamda);
			                 if (temp + time < timePeriod)
				                 time = temp + time;
			                 else if( temp >= timePeriod)
			                 {
			                	time = 0;
			                	over ++;
			                	if(EVENTLIST_DEBUG)
						             System.out.println("car generate time overflow" + over);
			                 }
			                 else if (temp + time >= timePeriod)
			            	    {time = 0;
			                     time = temp+time;
			                    }
			                 // time = time + Math.round(-Math.log(1 - r.nextFloat()) /
				             // lamda);
				             if(EVENTLIST_DEBUG)
					             System.out.println("car generate at " + time);
				             ArrayList<Event> list = new ArrayList<Event>();
					         list.add(new Event(Event.EventTypeEnum.CAR));
					         addToEventList(time, list);
                          }
				
			           }
			 }
			else 
			{
				int m = totalcar % entry;
				int et = totalcar - m;
				for (int event = 0; event < (et/entry); event++) 
		    	   { time = 0;
		             for (int e=0; e < entry;e++)
		              {  Random r = new Random();
		                 int temp =  (int) (-Math.log(1 - r.nextFloat()) / lamda);
		                 if (temp + time < timePeriod)
			                 time = temp + time;
		                 else 
		            	    {time = 0;
		                     time = temp+time;
		                    }
		                 // time = time + Math.round(-Math.log(1 - r.nextFloat()) /
			             // lamda);
			             if(EVENTLIST_DEBUG)
				             System.out.println("car generate at " + time);
			             ArrayList<Event> list = new ArrayList<Event>();
				         list.add(new Event(Event.EventTypeEnum.CAR));
				         addToEventList(time, list);
                   }
			
		           }
			   for (int event =0; event<m; event++)
			     {
				     time = 0;
				     Random r = new Random();
				     int temp =  (int) (-Math.log(1 - r.nextFloat()) / lamda);
	                 if (temp + time < timePeriod)
		                 time = temp + time;
	                 else if( temp >= timePeriod)
	                 {
	                	time = 0;
	                	over ++;
	                	if(EVENTLIST_DEBUG)
				             System.out.println("car generate time overflow" + over);
	                 }
	                 else if (temp + time > timePeriod)
	            	    { time = 0;
	                      time = temp+time;
	                    }
	                 
	                 if(EVENTLIST_DEBUG)
			             System.out.println("car generate at " + time);
		             ArrayList<Event> list = new ArrayList<Event>();
			         list.add(new Event(Event.EventTypeEnum.CAR));
			         addToEventList(time, list);
			     }
			  
			}
		}
	   return this.timePeriod;
	}

	public void addToEventList(long time, ArrayList<Event> list) {
		ArrayList<Event> listOfEvents = eList.get(time);

		if (listOfEvents == null) {
			listOfEvents = new ArrayList<Event>();
		}

		listOfEvents.addAll(list);
		eList.put(time, listOfEvents);
	}

	public ArrayList<Event> getFromEventList(long time) {
		return eList.get(time) != null ? (ArrayList<Event>) eList.get(time) : null;
	}

	/*
	 * This is quite similar to getFromEventList() Only difference, we have to
	 * update the Event status to "Completed"
	 */
	public void updateStatus(int time) {

	}

	public int getEventListSize(int time) {
		return eList.get(time).size();
	}
}