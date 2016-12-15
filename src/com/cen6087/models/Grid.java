package com.cen6087.models;

import java.util.ArrayList;
import java.util.Random;

import com.cen6087.models.TrafficLight.LightColor;
import com.cen6087.models.TrafficLight.TrafficDirection;

/*
 * Our grid looks like
 * ------>
 * <------
 * |^
 * ||
 * ||
 * v|
 * 
 */

/*
 *  Mao Ye, Prasun Dey, Atul John et al
 */
public class Grid {

	private int totalAvenues;
	private int totalStreets;
	private Intersection[][] gridMap;
	private boolean GRID_DEBUG = false;
	
	public Grid(int totalAvenues, int totalStreets) {
		super();
		this.totalAvenues = totalAvenues;
		this.totalStreets = totalStreets;
	}

	public void createGrid() {
		this.gridMap = new Intersection[totalAvenues][totalStreets];
		for (int i = 0; i < totalAvenues; i++) {
			for (int j = 0; j < totalStreets; j++) {
				this.gridMap[i][j] = new Intersection(i, j, totalAvenues, totalStreets);
			}
		}
		for (int i = 0; i < totalAvenues; i++) {
			for (int j = 0; j < totalStreets; j++) {
				ArrayList<Intersection> adjlist = findAdjIntersections(gridMap[i][j]);
				gridMap[i][j].setAdjIntersections(adjlist);
			}
		}
	}

	/*
	 * Returns entire Grid
	 */
	public int getTotalAvenues() {
		return totalAvenues;
	}

	public int getTotalStreets() {
		return totalStreets;
	}

	public Intersection[][] getGridMap() {
		return gridMap;
	}
	
	public Intersection[][] getIntersections() {
		return gridMap;
	}

	public void setIntersections(Intersection[][] intersections) {
		this.gridMap = intersections;
	}

	/*
	 * Returns the specific Intersection
	 */
	public Intersection getIntersection(int i, int j) {
		return gridMap[i][j];
	}

	/*
	 * Finds the adjacent 2 intersections where cars can move forward
	 */
	public ArrayList<Intersection> findAdjIntersections(Intersection intersection) {
		ArrayList<Intersection> nodes = new ArrayList<Intersection>();
		// Intersection[] nodes = new Intersection[2];
		int[] index = intersection.getIndex();
		int i = index[0];
		int j = index[1];

		if (intersection.getxDirection() == TrafficDirection.EW) {
			if (j - 1 != -1)
				nodes.add(getIntersection(i, j - 1));
			// nodes[0] = j - 1 != -1 ? getIntersection(i, j - 1) : null;
			if (intersection.getyDirection() == TrafficDirection.SN) {
				// nodes[1] = i - 1 != -1 ? getIntersection(i - 1, j) : null;
				if (i - 1 != -1)
					nodes.add(getIntersection(i - 1, j));
			} else {
				// nodes[1] = i + 1 != totalAvenues ? getIntersection(i + 1, j)
				// : null;
				if (i + 1 != totalAvenues)
					nodes.add(getIntersection(i + 1, j));
			}
		} else { // if (xDirection == TrafficDirection.WE)
			// nodes[0] = j + 1 != totalStreets ? getIntersection(i, j + 1) :
			// null;
			if (j + 1 != totalStreets)
				nodes.add(getIntersection(i, j + 1));
			if (intersection.getyDirection() == TrafficDirection.SN) {
				// nodes[1] = i - 1 != -1 ? getIntersection(i - 1, j) : null;
				if (i - 1 != -1)
					nodes.add(getIntersection(i - 1, j));
			} else {
				// nodes[1] = i + 1 != totalAvenues ? getIntersection(i + 1, j)
				// : null;
				if (i + 1 != totalAvenues)
					nodes.add(getIntersection(i + 1, j));
			}
		}
		return nodes;
	}

	/*
	 * This function should find the possible intersection list. When a car will
	 * be generated, it should take one intersection from the list and create
	 * accordingly.
	 */
	public ArrayList<Intersection> arrivalIntersectionList() {
		ArrayList<Intersection> list = new ArrayList<Intersection>();

		for (int j = 0; j < totalStreets; j++) {
			if (j % 2 == 0) {
				list.add(gridMap[0][j]);
			} else {
				list.add(gridMap[totalAvenues - 1][j]);
			}
		}

		for (int i = 1; i < totalAvenues; i++) {
			if (i % 2 != 0) {
				if (totalStreets % 2 == 0 && i != totalAvenues - 1)
					list.add(gridMap[i][totalStreets - 1]);
				else if (totalStreets % 2 != 0)
					list.add(gridMap[i][totalStreets - 1]);
			} else {
				list.add(gridMap[i][0]);
			}
		}

		return list;

	}

	/*
	 * This is the function that returns a random car path which only contains 0
	 * or 1 or 2 turning points.
	 */
	public ArrayList<Intersection> pathfinder(ArrayList<Intersection> arrivalIntersectionList) {
		Random rand = new Random();
		//int random = rand.nextInt(3); // random variable that has value either
										// 0, 1 or (2)
		int random = 2;
		ArrayList<Intersection> path = new ArrayList<Intersection>();
		int entrypoint = rand.nextInt(arrivalIntersectionList.size() - 1); // randomly
																			// pick
																			// an
																			// index
																			// from
																			// arrivalIntersectionList
		// add the intersection with the above index to the carpath as the entry
		// point
		path.add((Intersection) arrivalIntersectionList.get(entrypoint));
		Intersection start = path.get(0);
		int[] index = start.getIndex(); // get entry point index in Grid ( Map )
		if(GRID_DEBUG)
			System.out.println("path +" + index[0] + index[1]);
		// ArrayList<Intersection> adj = start.getAdjIntersections();
		TrafficDirection x = start.getxDirection(); // get entry point direction
													// in x-axis
		TrafficDirection y = start.getyDirection(); // get entry point direction
													// in y-axis
		Intersection[][] Map = getIntersections(); // get
		int i;
		int j;
		// get direction
		i = index[0]; // y-axis index,
		j = index[1]; // x-axis index
		Random ran = new Random(); // a random number
		int offset; // the random offset for first turn ( distance between entry
					// point to first turn )
		int end; // the variable with the altered index of first turn in either
					// x or y axis
		int off; // the variable which used to take the value of i or j
		int offset1; // the random offset for second turn ( distance between
						// first turn to second turn)

		switch (random) {
		case 0:

			// The arrival nodes from [x][0], include [0][0]
			if (y == TrafficDirection.NS && index[0] == 0) {
				if (index[0] == 0 && index[1] == 0) {
					Random randnode = new Random();
					int nodeindex = randnode.nextInt(2); // change from 1 to 2
					switch (nodeindex) { // Map[0][0] has two choices
					case 0: // Go from N to S
						j = index[1];
						i = index[0] + 1;
						while (i <= totalAvenues - 1) {
							path.add(Map[i][j]);
							i++;
						}
						break;
					case 1: // Go from W to E
						j = index[1] + 1;
						i = index[0];
						while (j <= totalStreets - 1) {
							path.add(Map[i][j]);
							j++;
						}
						break;
					}
				}

				else// Go from N to S
				{
					j = index[1];
					i = index[0] + 1;
					while (i <= totalAvenues - 1) {
						path.add(Map[i][j]);
						i++;
					}
				}
			}
			// The arrival nodes from [x][totalAvenues-1],include
			// [totalStreets-1][totalAvenues-1]
			else if (y == TrafficDirection.SN && index[0] == totalAvenues - 1) {
				if (index[1] == totalStreets - 1 && x == TrafficDirection.EW) {
					Random randnode = new Random();
					int nodeindex = randnode.nextInt(2); //change from 1 to 2
					switch (nodeindex) {
					case 0:
						i = index[0];
						j = index[1] - 1;
						while (j >= 0) {
							path.add(Map[i][j]);
							j--;
						}
						break;
					case 1:
						i = index[0] - 1;
						j = index[1];
						while (i >= 0) {
							path.add(Map[i][j]);
							i--;
						}
						break;
					}
				} else {
					i = index[0] - 1;
					j = index[1];
					while (i >= 0) {
						path.add(Map[i][j]);
						i--;
					}
				}
			}
			// The arrival nodes [0][2]~[0][y]
			else if (x == TrafficDirection.WE && index[1] == 0 && index[0] >= 2) {
				j = index[1] + 1;
				i = index[0];
				while (j <= totalStreets - 1) {
					path.add(Map[i][j]);
					j++;
				}
			} else if (x == TrafficDirection.EW && index[1] == totalStreets - 1) {
				j = index[1] - 1;
				i = index[0];
				while (j >= 0) {
					path.add(Map[i][j]);
					j--;
				}

			}
			break;

		case 1: // only 1 turn
			// The arrival nodes from [0][x], include [0][0]

			if (y == TrafficDirection.NS && index[0] == 0) {
				offset = ran.nextInt(totalAvenues); // change to totalAvenues
				end = i + offset;

				// 1.entry point from y axis, direction NS, first turn on y axis
				off = i;
				while (off < end) {
					off++;
					path.add(Map[off][j]);
				}
				if (Map[off][j].getxDirection() == TrafficDirection.WE) {
					while (j < totalStreets - 1) {
						j++;
						path.add(Map[off][j]);
					}
					break;
				} else {
					while (j > 0) {
						j--;
						path.add(Map[off][j]);
					}
					break;
				}

			}
			// 2. entry point from y axis, direction SN, first turn on y axis
			else if (y == TrafficDirection.SN && index[0] == totalAvenues - 1) {
				offset = ran.nextInt(totalAvenues); // change to totalAvenues
				end = i - offset;
				off = i;
				while (off > end) {
					off--;
					path.add(Map[off][j]);
				}
				if (Map[off][j].getxDirection() == TrafficDirection.WE) {
					while (j < totalStreets - 1) {
						j++;
						path.add(Map[off][j]);
					}
					break;
				} else {
					while (j > 0) {
						j--;
						path.add(Map[off][j]);
					}
					break;
				}

			}
			// 3. entry point at x axis,direction WE, first turn on x axis
			else if (x == TrafficDirection.WE && index[1] == 0) {
				offset = ran.nextInt(totalStreets); // change to totalStreets from totalStreets -1
				end = j + offset;
				off = j;
				while (off < end) {
					off++;
					path.add(Map[i][off]);
				}
				if (Map[i][off].getyDirection() == TrafficDirection.NS) {
					while (i < totalAvenues - 1) {
						i++;
						path.add(Map[i][off]);
					}
					break;
				} else {
					while (i > 0) {
						i--;
						path.add(Map[i][off]);
					}
					break;
				}

			}
			// 4. entry point at x axis,direction EW, first turn on x axis
			else if (x == TrafficDirection.EW && index[1] == totalStreets - 1) {
				offset = ran.nextInt(totalStreets); // change to totalStreets 
				end = j - offset;
				off = j;
				while (off > end) {
					off--;
					path.add(Map[i][off]);
				}
				if (Map[i][off].getyDirection() == TrafficDirection.NS) {
					while (i < totalAvenues - 1) {
						i++;
						path.add(Map[i][off]);
					}
					break;
				} else {
					while (i > 0) {
						i--;
						path.add(Map[i][off]);
					}
					break;
				}
			}

		case 2: // only 2 turns
			i = index[0];
			j = index[1];
			int adjnum = 0; // Initialize the first turn's adj-element number
			// A variable equal to x index or y index
			int t; // Second turn index variable, either x or y

			boolean flag = false;
			if (y == TrafficDirection.NS && index[0] == 0) {
				off = i;
				offset = ran.nextInt(totalAvenues - 1);
				end = i + offset;
				while (flag == false) {
					offset = ran.nextInt(totalAvenues - 1);
					end = i + offset;
					off = i;
					ArrayList<Intersection> adj_element = Map[end][j].getAdjIntersections();
					adjnum = adj_element.size();
					if (adjnum == 0) {
						flag = false;
					} else if (j == 0 && Map[end][j].getxDirection() == TrafficDirection.EW) {
						flag = false;
					} else if (j == totalStreets - 1 && Map[end][j].getxDirection() == TrafficDirection.WE) {
						flag = false;
					} else {
						flag = true;
					}
				}
				while (off < end) {
					off++;
					path.add(Map[off][j]);// add until the first turn
				}
				if(GRID_DEBUG)
					System.out.println("First turning is [" + end + j + "]");
				if (Map[off][j].getxDirection() == TrafficDirection.WE) {
					offset1 = ran.nextInt(totalStreets - 1 - j) + 1;
					t = j;
					while (offset1 > 0) {
						t++;
						path.add(Map[off][t]);
						offset1--;
					}
					if (Map[off][t].getyDirection() == TrafficDirection.NS) {
						while (off < totalAvenues - 1) {
							off++;
							path.add(Map[off][t]);
						}
						break;
					} else {
						while (off > 0) {
							off--;
							path.add(Map[off][t]);
						}
						break;
					}

				} else {
					offset1 = ran.nextInt(j) + 1;
					t = j;
					while (offset1 > 0) {
						t--;
						path.add(Map[off][t]);
						offset1--;
					}
					if (Map[off][t].getyDirection() == TrafficDirection.NS) {
						while (off < totalAvenues - 1) {
							off++;
							path.add(Map[off][t]);
						}
						break;
					} else {
						while (off > 0) {
							off--;
							path.add(Map[off][t]);
						}
						break;
					}
				}

			}

			else if (y == TrafficDirection.SN && index[0] == totalAvenues - 1) {
				off = i;
				offset = ran.nextInt(totalAvenues - 1);
				end = i + offset;
				while (flag == false) {
					offset = ran.nextInt(totalAvenues - 1);
					end = i - offset;
					off = i;
					ArrayList<Intersection> adj_element = Map[end][j].getAdjIntersections();
					adjnum = adj_element.size();
					if (adjnum == 0) {
						flag = false;
					} else if (j == totalStreets - 1 && Map[end][j].getxDirection() == TrafficDirection.WE) {
						flag = false;
					} else {
						flag = true;
					}
				}
				while (off > end) {
					off--;
					path.add(Map[off][j]);
				}
				if (Map[off][j].getxDirection() == TrafficDirection.WE) {
					offset1 = ran.nextInt(totalStreets - 1 - j) + 1;
					t = j;
					while (offset1 > 0) {
						t++;
						path.add(Map[off][t]);
						offset1--;
					}
					if (Map[off][t].getyDirection() == TrafficDirection.NS) {
						while (off < totalAvenues - 1) {
							off++;
							path.add(Map[off][t]);
						}
						break;
					} else {
						while (off > 0) {
							off--;
							path.add(Map[off][t]);
						}
						break;
					}
				} else {
					offset1 = ran.nextInt(j) + 1;
					t = j;
					while (offset1 > 0) {
						t--;
						path.add(Map[off][t]);
						offset1--;
					}
					if (Map[off][t].getyDirection() == TrafficDirection.NS) {
						while (off < totalAvenues - 1) {
							off++;
							path.add(Map[off][t]);
						}
						break;
					} else {
						while (off > 0) {
							off--;
							path.add(Map[off][t]);
						}
						break;
					}
				}
			}

			else if (x == TrafficDirection.WE && index[1] == 0) {
				offset = ran.nextInt(totalStreets - 1);
				end = j + offset;
				off = i;

				while (flag == false) {
					offset = ran.nextInt(totalStreets - 1);
					end = j + offset;
					off = i;
					ArrayList<Intersection> adj_element = Map[end][j].getAdjIntersections();
					adjnum = adj_element.size();
					if (adjnum == 0) {
						flag = false;
					} else if (i == 0 && Map[end][j].getxDirection() != TrafficDirection.SN) {
						flag = false;
					} else if (i == totalAvenues - 1 && Map[end][j].getxDirection() == TrafficDirection.NS) {
						flag = false;
					} else {
						flag = true;
					}
				}
				while (off < end) {
					off++;
					path.add(Map[i][off]);
				}
				if (Map[i][off].getyDirection() == TrafficDirection.NS) {
					offset1 = ran.nextInt(totalAvenues - 1 - i + 1) + 1;
					t = i;
					while (offset1 > 0) {
						t++;
						path.add(Map[t][off]);
						offset1--;
					}
					if (Map[t][off].getxDirection() == TrafficDirection.WE) {
						while (off < totalStreets - 1) {
							off++;
							path.add(Map[t][off]);
						}
						break;
					} else {
						while (off > 0) {
							off--;
							path.add(Map[t][off]);
							break;
						}
					}
				} else {
					offset1 = ran.nextInt(i - 1) + 1;
					t = i;
					while (offset1 > 0) {
						t--;
						path.add(Map[t][off]);
						offset1--;
					}
					if (Map[t][off].getxDirection() == TrafficDirection.WE) {
						while (off < totalStreets - 1) {
							off++;
							path.add(Map[t][off]);
						}
						break;
					} else {
						while (off > 0) {
							off--;
							path.add(Map[t][off]);
							break;
						}
					}
				}
			}

			else if (x == TrafficDirection.EW && index[1] == totalStreets - 1) {
				offset = ran.nextInt(totalStreets - 1);
				end = j + offset;
				off = j;
				while (flag == false) {
					offset = ran.nextInt(totalStreets - 1);
					end = j - offset;
					off = j;
					ArrayList<Intersection> adj_element = Map[end][j].getAdjIntersections();
					adjnum = adj_element.size();
					if (adjnum == 0) {
						flag = false;
					} else if (i == totalAvenues - 1 && Map[end][j].getxDirection() == TrafficDirection.NS) {
						flag = false;
					} else {
						flag = true;
					}
				}

				while (off > end) {
					off--;
					path.add(Map[i][off]);
				}
				if(GRID_DEBUG)
					System.out.println("the first turn is " + i + off);

				if (Map[i][off].getyDirection() == TrafficDirection.NS) {
					offset1 = rand.nextInt(totalAvenues - 1 - i) + 1;
					t = i;
					while (offset1 > 0) {
						t++;
						path.add(Map[t][off]);
						offset1--;
					}
					if (Map[t][off].getxDirection() == TrafficDirection.WE) {
						while (off < totalStreets - 1) {
							off++;
							path.add(Map[t][off]);
						}
						break;
					} else {
						while (off > 0) {
							off--;
							path.add(Map[t][off]);
							break;
						}
					}
				} else { // System.out.println("i" +i );
					offset1 = ran.nextInt(i) + 1;
					// System.out.println("offset1 is "+offset1);
					t = i;
					while (offset1 > 0) {
						t--;
						path.add(Map[t][off]);
						offset1--;
					}
					if (Map[t][off].getxDirection() == TrafficDirection.WE) {
						while (off < totalStreets - 1) {
							off++;
							path.add(Map[t][off]);
						}
						break;
					} else {
						while (off > 0) {
							off--;
							path.add(Map[t][off]);
							break;
						}
					}
				}

			}
			// break;
		}

		// if(GRID_DEBUG)
		// for(Intersection item:path)
		// { System.out.println("node [" + item.getIndex()+"]"); }
		return path;
	}

	public ArrayList<TrafficLight> lightpathfinder(ArrayList<Intersection> arrivalIntersectionList) {
		Random rand = new Random();
	//	int random = rand.nextInt(1); // random variable that has value either
		int random = rand.nextInt(3); // random variable that has value either
										// 0, 1 or (2).
		//int random = 0;
		ArrayList<Intersection> path = new ArrayList<Intersection>();
		ArrayList<TrafficLight> turns = new ArrayList<TrafficLight>();
		// randomly pick an index from arrivalIntersectionList
		int entrypoint = rand.nextInt(arrivalIntersectionList.size() - 1);

		// add the intersection with the above index to the carpath as entry
		// point
		path.add((Intersection) arrivalIntersectionList.get(entrypoint));
		ArrayList<TrafficLight> lightpath = new ArrayList<TrafficLight>();
		Intersection start = path.get(0);
		int[] index = start.getIndex(); // get entry point index in Grid ( Map )
		if (GRID_DEBUG) {
			System.out.println("Entry path: " + index[0] + index[1]);
		}
		// ArrayList<Intersection> adj = start.getAdjIntersections();
		TrafficDirection x = start.getxDirection(); // get entry point direction
													// in x-axis
		TrafficDirection y = start.getyDirection(); // get entry point direction
													// in y-axis
		Intersection[][] Map = getIntersections(); // get
		int i;
		int j;
		// get direction
		i = index[0]; // y-axis index,
		j = index[1]; // x-axis index
		Random ran = new Random(); // a random number
		int offset; // the random offset for first turn ( distance between entry
					// point to first turn )
		int end; // the variable with the altered index of first turn in either
					// x or y axis
		int off; // the variable which used to take the value of i or j
		int offset1; // the random offset for second turn ( distance between
						// first turn to second turn)

		switch (random) {
		case 0:

			// The arrival nodes from [x][0], include [0][0]
			if (y == TrafficDirection.NS && index[0] == 0)

			{

				if (index[0] == 0 && index[1] == 0) {
					Random randnode = new Random();
					int nodeindex = randnode.nextInt(2); // change from 1 to 2
					switch (nodeindex) { // Map[0][0] has two choices
					case 0: // Go from N to S
						TrafficLight light = new TrafficLight(start, TrafficDirection.NS, "light2");
						lightpath.add(start.getLight2());
						j = index[1];
						i = index[0] + 1;
						while (i <= totalAvenues - 1) {
							path.add(Map[i][j]);
							light = new TrafficLight(Map[i][j], TrafficDirection.NS, "light2");
							lightpath.add(Map[i][j].getLight2());
							i++;
						}
						break;
					case 1: // Go from W to E
						light = new TrafficLight(start, TrafficDirection.WE, "light1");
						lightpath.add(start.getLight1());
						j = index[1] + 1;
						i = index[0];
						while (j <= totalStreets - 1) {
							path.add(Map[i][j]);
							light = new TrafficLight(Map[i][j], TrafficDirection.WE, "light1");
							lightpath.add(Map[i][j].getLight1());
							j++;
						}
						break;
					}
				}

				else// Go from N to S
				{
					TrafficLight light = new TrafficLight(start, TrafficDirection.NS, "light2");
					lightpath.add(start.getLight2());
					j = index[1];
					i = index[0] + 1;
					while (i <= totalAvenues - 1) {
						path.add(Map[i][j]);
						light = new TrafficLight(Map[i][j], TrafficDirection.NS, "light2");
						lightpath.add(Map[i][j].getLight2());
						i++;
					}
				}
			}
			// The arrival nodes from [x][totalAvenues-1],include
			// [totalStreets-1][totalAvenues-1]
			else if (y == TrafficDirection.SN && index[0] == totalAvenues - 1) {
				if (index[1] == totalStreets - 1 && x == TrafficDirection.EW) {
					Random randnode = new Random();
					int nodeindex = randnode.nextInt(2); //change from 1 to 2
					switch (nodeindex) {
					case 0:
						TrafficLight light = new TrafficLight(start, TrafficDirection.EW, "light1");
						lightpath.add(start.getLight1());
						i = index[0];
						j = index[1] - 1;
						while (j >= 0) {
							path.add(Map[i][j]);
							light = new TrafficLight(Map[i][j], TrafficDirection.EW, "light1");
							lightpath.add(Map[i][j].getLight1());
							j--;
						}
						break;
					case 1:
						light = new TrafficLight(start, TrafficDirection.SN, "light2");
						lightpath.add(start.getLight2());
						i = index[0] - 1;
						j = index[1];
						while (i >= 0) {
							path.add(Map[i][j]);
							light = new TrafficLight(Map[i][j], TrafficDirection.SN, "light2");
							lightpath.add(Map[i][j].getLight2());
							i--;
						}
						break;
					}
				} else {
					TrafficLight light = new TrafficLight(start, TrafficDirection.SN, "light2");
					lightpath.add(start.getLight2());
					i = index[0] - 1;
					j = index[1];
					while (i >= 0) {
						path.add(Map[i][j]);
						light = new TrafficLight(Map[i][j], TrafficDirection.SN, "light2");
						lightpath.add(Map[i][j].getLight2());
						i--;
					}
				}
			}
			// The arrival nodes [0][2]~[0][y]
			else if (x == TrafficDirection.WE && index[1] == 0 && index[0] >= 2) {
				TrafficLight light = new TrafficLight(start, TrafficDirection.WE, "light1");
				lightpath.add(start.getLight1());
				j = index[1] + 1;
				i = index[0];
				while (j <= totalStreets - 1) {
					path.add(Map[i][j]);
					light = new TrafficLight(Map[i][j], TrafficDirection.WE, "light1");
					lightpath.add(Map[i][j].getLight1());
					j++;
				}
			} else if (x == TrafficDirection.EW && index[1] == totalStreets - 1) {
				TrafficLight light = new TrafficLight(start, TrafficDirection.EW, "light1");
				lightpath.add(start.getLight1());
				j = index[1] - 1;
				i = index[0];
				while (j >= 0) {
					path.add(Map[i][j]);
					light = new TrafficLight(Map[i][j], TrafficDirection.EW, "light1");
					lightpath.add(Map[i][j].getLight1());
					j--;
				}

			}
			break;

		case 1: // only 1 turn
			// The arrival nodes from [0][x], include [0][0]

			if (y == TrafficDirection.NS && index[0] == 0) {
				TrafficLight light = new TrafficLight(start, TrafficDirection.NS, "light2");
				lightpath.add(start.getLight2());
				offset = ran.nextInt(totalAvenues); //change from -1 to totalAvenues
				end = i + offset;
				// 1.entry point from y axis, direction NS, first turn on y axis
				off = i;
				while (off < end) {
					off++;
					path.add(Map[off][j]);
					light = new TrafficLight(Map[off][j], TrafficDirection.NS, "light2");
					lightpath.add(Map[off][j].getLight2());
				} 
				if (Map[off][j].getxDirection() == TrafficDirection.WE) {
					//light = new TrafficLight(Map[off][j], TrafficDirection.WE, "light1");
					//lightpath.add(Map[off][j].getLight1());
					turns.add(Map[off][j].getLight1());
					while (j < totalStreets - 1) {
						j++;
						path.add(Map[off][j]);
						light = new TrafficLight(Map[off][j], TrafficDirection.WE, "light1");
						lightpath.add(Map[off][j].getLight1());
					}
					break;
				} else {
					//light = new TrafficLight(Map[off][j], TrafficDirection.EW, "light1");
					//lightpath.add(Map[off][j].getLight1());
					turns.add(Map[off][j].getLight1());
					while (j > 0) {
						j--;
						path.add(Map[off][j]);
						light = new TrafficLight(Map[off][j], TrafficDirection.EW, "light1");
						lightpath.add(Map[off][j].getLight1());
					}
					break;
				}

			}
			// 2. entry point from y axis, direction SN, first turn on y axis
			else if (y == TrafficDirection.SN && index[0] == totalAvenues - 1) {
				TrafficLight light = new TrafficLight(start, TrafficDirection.SN, "light2");
				lightpath.add(start.getLight2());
				offset = ran.nextInt(totalAvenues); // change from -1 to totalAvenues
				end = i - offset;
				off = i;
				while (off > end) {
					off--;
					path.add(Map[off][j]);
					light = new TrafficLight(Map[off][j], TrafficDirection.SN, "light2");
					lightpath.add(Map[off][j].getLight2());
				}
				if (Map[off][j].getxDirection() == TrafficDirection.WE) {
					//light = new TrafficLight(Map[off][j], TrafficDirection.WE, "light1");
					//lightpath.add(Map[off][j].getLight1());
					turns.add(Map[off][j].getLight1());
					while (j < totalStreets - 1) {
						j++;
						path.add(Map[off][j]);
						light = new TrafficLight(Map[off][j], TrafficDirection.WE, "light1");
						lightpath.add(Map[off][j].getLight1());
					}
					break;
				} else {
					//light = new TrafficLight(Map[off][j], TrafficDirection.EW, "light1");
					//lightpath.add(Map[off][j].getLight1());
					turns.add(Map[off][j].getLight1());
					while (j > 0) {
						j--;
						path.add(Map[off][j]);
						light = new TrafficLight(Map[off][j], TrafficDirection.EW, "light1");
						lightpath.add(Map[off][j].getLight1());
					}
					break;
				}

			}
			// 3. entry point at x axis,direction WE, first turn on x axis
			else if (x == TrafficDirection.WE && index[1] == 0) {
				TrafficLight light = new TrafficLight(start, TrafficDirection.WE, "light1");
				lightpath.add(start.getLight1());
				offset = ran.nextInt(totalStreets - 1);
				end = j + offset;
				off = j;
				while (off < end) {
					off++;
					path.add(Map[i][off]);
					light = new TrafficLight(Map[i][off], TrafficDirection.WE, "light1");
					lightpath.add(Map[i][off].getLight1());
				}
				if (Map[i][off].getyDirection() == TrafficDirection.NS) {
				//	light = new TrafficLight(Map[i][off], TrafficDirection.NS, "light2");
				//	lightpath.add(Map[i][off].getLight1());
					turns.add(Map[i][off].getLight2());
					while (i < totalAvenues - 1) {
						i++;
						path.add(Map[i][off]);
						light = new TrafficLight(Map[i][off], TrafficDirection.NS, "light2");
						lightpath.add(Map[i][off].getLight2());
					}
					break;
				} else {
					//light = new TrafficLight(Map[i][off], TrafficDirection.SN, "light2");
					//lightpath.add(Map[i][off].getLight2());
					turns.add(Map[i][off].getLight2());
					while (i > 0) {
						i--;
						path.add(Map[i][off]);
						light = new TrafficLight(Map[i][off], TrafficDirection.SN, "light2");
						lightpath.add(Map[i][off].getLight2());
					}
					break;
				}

			}
			// 4. entry point at x axis,direction EW, first turn on x axis
			else if (x == TrafficDirection.EW && index[1] == totalStreets - 1) {
				TrafficLight light = new TrafficLight(start, TrafficDirection.EW, "light1");
				lightpath.add(start.getLight1());
				offset = ran.nextInt(totalStreets - 1); // change from -1 to totalStreets
				end = j - offset;
				off = j;
				while (off > end) {
					off--;
					path.add(Map[i][off]);
					light = new TrafficLight(Map[i][off], TrafficDirection.EW, "light1");
					lightpath.add(Map[i][off].getLight1());
				}
				if (Map[i][off].getyDirection() == TrafficDirection.NS) {
				//	light = new TrafficLight(Map[i][off], TrafficDirection.NS, "light2");
				//	lightpath.add(Map[i][off].getLight2());
					turns.add(Map[i][off].getLight2());	
					while (i < totalAvenues - 1) {
						i++;
						path.add(Map[i][off]);
						light = new TrafficLight(Map[i][off], TrafficDirection.NS, "light2");
						lightpath.add(Map[i][off].getLight2());
					}
					break;
				} else {
					//light = new TrafficLight(Map[i][off], TrafficDirection.SN, "light2");
					//lightpath.add(Map[i][off].getLight2());
					turns.add(Map[i][off].getLight2());
					while (i > 0) {
						i--;
						path.add(Map[i][off]);
						light = new TrafficLight(Map[i][off], TrafficDirection.SN, "light2");
						lightpath.add(Map[i][off].getLight2());
					}
					break;
				}
			}

		case 2: // only 2 turns
			i = index[0];
			j = index[1];
			int adjnum = 0; // Initialize the first turn's adj-element number
			// A variable equal to x index or y index
			int t; // Second turn index variable, either x or y

			boolean flag = false;
			if (y == TrafficDirection.NS && index[0] == 0) {
				TrafficLight light = new TrafficLight(start, TrafficDirection.NS, "light2");
				lightpath.add(start.getLight2());
				off = i;
				offset = ran.nextInt(totalAvenues); // change from -1 to totalAvenues;
				end = i + offset;
				while (flag == false) {
					offset = ran.nextInt(totalAvenues); //change from -1 to totalAvenues;
					end = i + offset;
					off = i;
					ArrayList<Intersection> adj_element = Map[end][j].getAdjIntersections();
					adjnum = adj_element.size();
					if (adjnum == 0) {
						flag = false;
					} else if (j == 0 && Map[end][j].getxDirection() == TrafficDirection.EW) {
						flag = false;
					} else if (j == totalStreets - 1 && Map[end][j].getxDirection() == TrafficDirection.WE) {
						flag = false;
					} else {
						flag = true;
					}
				}
				while (off < end) {
					off++;
					path.add(Map[off][j]);// add until the first turn
					light = new TrafficLight(Map[off][j], TrafficDirection.NS, "light2");
					lightpath.add(Map[off][j].getLight2());
				}
				if(GRID_DEBUG)
					System.out.println("First turning is [" + end + j + "]");
				if (Map[off][j].getxDirection() == TrafficDirection.WE) {
					//light = new TrafficLight(Map[off][j], TrafficDirection.WE, "light1");
					//lightpath.add(Map[off][j].getLight1());
					turns.add(Map[off][j].getLight1());	
					offset1 = ran.nextInt(totalStreets - j); // change from -1 -j to -j
					if (offset1 == 0)
						offset1 = 1;
					t = j;
					while (offset1 > 0) {
						t++;
						path.add(Map[off][t]);
						light = new TrafficLight(Map[off][t], TrafficDirection.WE, "light1");
						lightpath.add(Map[off][t].getLight1());
						offset1--;
					}
					if (Map[off][t].getyDirection() == TrafficDirection.NS) {
						//light = new TrafficLight(Map[off][t], TrafficDirection.NS, "light2");
						//lightpath.add(Map[off][t].getLight2());
						turns.add(Map[off][j].getLight2());
						while (off < totalAvenues - 1) {
							off++;
							path.add(Map[off][t]);
							light = new TrafficLight(Map[off][t], TrafficDirection.NS, "light2");
							lightpath.add(Map[off][t].getLight2());
						}
						break;
					} else {
						//light = new TrafficLight(Map[off][t], TrafficDirection.SN, "light2");
						//lightpath.add(Map[off][t].getLight2());
						turns.add(Map[off][j].getLight2());
						while (off > 0) {
							off--;
							path.add(Map[off][t]);
							light = new TrafficLight(Map[off][t], TrafficDirection.SN, "light2");
							lightpath.add(Map[off][t].getLight2());
						}
						break;
					}

				} else {
					//light = new TrafficLight(Map[off][j], TrafficDirection.EW, "light1");
					//lightpath.add(Map[off][j].getLight1());
					turns.add(Map[off][j].getLight1());
					offset1 = ran.nextInt(j);
					if (offset1 == 0)
						offset1 = 1;
					t = j;
					while (offset1 > 0) {
						t--;
						path.add(Map[off][t]);
						light = new TrafficLight(Map[off][t], TrafficDirection.EW, "light1");
						lightpath.add(Map[off][t].getLight1());
						offset1--;
					}
					if (Map[off][t].getyDirection() == TrafficDirection.NS) {
					//	light = new TrafficLight(Map[off][t], TrafficDirection.NS, "light2");
					//	lightpath.add(Map[off][t].getLight2());
						turns.add(Map[off][t].getLight2());
						while (off < totalAvenues - 1) {
							off++;
							path.add(Map[off][t]);
							light = new TrafficLight(Map[off][t], TrafficDirection.NS, "light2");
							lightpath.add(Map[off][t].getLight2());
						}
						break;
					} else {
						//light = new TrafficLight(Map[off][t], TrafficDirection.SN, "light2");
						//lightpath.add(Map[off][t].getLight2());
						turns.add(Map[off][t].getLight2());
						while (off > 0) {
							off--;
							path.add(Map[off][t]);
							light = new TrafficLight(Map[off][t], TrafficDirection.SN, "light2");
							lightpath.add(Map[off][t].getLight2());
						}
						break;
					}
				}

			}

			else if (y == TrafficDirection.SN && index[0] == totalAvenues - 1) {
				TrafficLight light = new TrafficLight(start, TrafficDirection.SN, "light2");
				lightpath.add(start.getLight2());
				off = i;
				offset = ran.nextInt(totalAvenues - 1);
				end = i + offset;
				while (flag == false) {
					offset = ran.nextInt(totalAvenues - 1);
					end = i - offset;
					off = i;
					ArrayList<Intersection> adj_element = Map[end][j].getAdjIntersections();
					adjnum = adj_element.size();
					if (adjnum == 0) {
						flag = false;
					} else if (j == totalStreets - 1 && Map[end][j].getxDirection() == TrafficDirection.WE) {
						flag = false;
					} else {
						flag = true;
					}
				}
				while (off > end) {
					off--;
					path.add(Map[off][j]);
					light = new TrafficLight(Map[off][j], TrafficDirection.SN, "light2");
					lightpath.add(Map[off][j].getLight2());
				}
				if (Map[off][j].getxDirection() == TrafficDirection.WE) {
					//light = new TrafficLight(Map[off][j], TrafficDirection.WE, "light1");
					//lightpath.add(Map[off][j].getLight1());
					turns.add(Map[off][j].getLight1());
					offset1 = ran.nextInt(totalStreets - j ) ;
					// modify from -1 -j to -j
					if (offset1 == 0)
						offset1 = 1;
					t = j;
					while (offset1 > 0) {
						t++;
						path.add(Map[off][t]);
						light = new TrafficLight(Map[off][t], TrafficDirection.WE, "light1");
						lightpath.add(Map[off][t].getLight1());
						offset1--;
					}
					if (Map[off][t].getyDirection() == TrafficDirection.NS) {
					//	light = new TrafficLight(Map[off][t], TrafficDirection.NS, "light2");
					//	lightpath.add(Map[off][t].getLight2());
						turns.add(Map[off][t].getLight2());
						while (off < totalAvenues - 1) {
							off++;
							path.add(Map[off][t]);
							light = new TrafficLight(Map[off][t], TrafficDirection.NS, "light2");
							lightpath.add(Map[off][t].getLight2());
						}
						break;
					} else {
						//light = new TrafficLight(Map[off][t], TrafficDirection.SN, "light2");
						//lightpath.add(Map[off][t].getLight2());
						turns.add(Map[off][t].getLight2());
						while (off > 0) {
							off--;
							path.add(Map[off][t]);
							light = new TrafficLight(Map[off][t], TrafficDirection.NS, "light2");
							lightpath.add(Map[off][t].getLight2());
						}
						break;
					}
				} else {
					//light = new TrafficLight(Map[off][j], TrafficDirection.EW, "light1");
					//lightpath.add(Map[off][j].getLight1());
					turns.add(Map[off][j].getLight1());
					offset1 = ran.nextInt(j); // modify form j -1 to j
					if (offset1 == 0 )
						offset1 = 1;
					t = j;
					while (offset1 > 0) {
						t--;
						path.add(Map[off][t]);
						light = new TrafficLight(Map[off][t], TrafficDirection.EW, "light1");
						lightpath.add(Map[off][t].getLight1());
						offset1--;
					}
					if (Map[off][t].getyDirection() == TrafficDirection.NS) {
					//	light = new TrafficLight(Map[off][t], TrafficDirection.NS, "light2");
					//	lightpath.add(Map[off][t].getLight2());
						turns.add(Map[off][t].getLight1());	
						while (off < totalAvenues - 1) {
							off++;
							path.add(Map[off][t]);
							light = new TrafficLight(Map[off][t], TrafficDirection.NS, "light2");
							lightpath.add(Map[off][t].getLight2());
						}
						break;
					} else {
					//	light = new TrafficLight(Map[off][t], TrafficDirection.SN, "light2");
					//	lightpath.add(Map[off][t].getLight2());
						turns.add(Map[off][t].getLight2());		
						while (off > 0) {
							off--;
							path.add(Map[off][t]);
							light = new TrafficLight(Map[off][t], TrafficDirection.SN, "light2");
							lightpath.add(Map[off][t].getLight2());
						}
						break;
					}
				}
			}

			else if (x == TrafficDirection.WE && index[1] == 0) {
				TrafficLight light = new TrafficLight(start, TrafficDirection.WE, "light1");
				lightpath.add(start.getLight1());
				offset = ran.nextInt(totalStreets); // modify from -1 to totalStreets
				end = j + offset; // modify i + offset to j+ offset
				off = j; // modify i to j

				while (flag == false) {
					offset = ran.nextInt(totalStreets - 1);
					end = j + offset; // modify i + offset to j + offset
					off = j; // modify i to j
					ArrayList<Intersection> adj_element = Map[end][j].getAdjIntersections();
					adjnum = adj_element.size();
					if (adjnum == 0) {
						flag = false;
					} else if (i == 0 && Map[end][j].getxDirection() != TrafficDirection.SN) {
						flag = false;
					} else if (i == totalAvenues - 1 && Map[end][j].getxDirection() == TrafficDirection.NS) {
						flag = false;
					} else {
						flag = true;
					}
				}
				while (off < end) {
					off++;
					path.add(Map[i][off]);
					light = new TrafficLight(Map[i][off], TrafficDirection.WE, "light1");
					lightpath.add(Map[i][off].getLight1());
				}
				if (Map[i][off].getyDirection() == TrafficDirection.NS) {
					//light = new TrafficLight(Map[i][off], TrafficDirection.NS, "light2");
					//lightpath.add(Map[i][off].getLight2());
					turns.add(Map[i][off].getLight2());	
					offset1 = ran.nextInt(totalAvenues - i ) ; // remove  -1 
					t = i;
					while (offset1 > 0) {
						t++;
						path.add(Map[t][off]);
						light = new TrafficLight(Map[t][off], TrafficDirection.NS, "light2");
						lightpath.add(Map[t][off].getLight2());
						offset1--;
					}
					if (Map[t][off].getxDirection() == TrafficDirection.WE) {
					//	light = new TrafficLight(Map[t][off], TrafficDirection.WE, "light1");
					//	lightpath.add(Map[t][off].getLight1());
						turns.add(Map[t][off].getLight1());
						while (off < totalStreets - 1) {
							off++;
							path.add(Map[t][off]);
							light = new TrafficLight(Map[t][off], TrafficDirection.WE, "light1");
							lightpath.add(Map[t][off].getLight1());
						}
						break;
					} else {
					//	light = new TrafficLight(Map[t][off], TrafficDirection.EW, "light1");
					//	lightpath.add(Map[t][off].getLight1());
						turns.add(Map[t][off].getLight1());
						while (off > 0) {
							off--;
							path.add(Map[t][off]);
							light = new TrafficLight(Map[t][off], TrafficDirection.EW, "light1");
							lightpath.add(Map[t][off].getLight1());
							break;
						}
					}
				} else {
				//	light = new TrafficLight(Map[i][off], TrafficDirection.SN, "light2");
				//	lightpath.add(Map[i][off].getLight2());
					turns.add(Map[i][off].getLight2());
					offset1 = ran.nextInt(i) ; 
					// remove -1 
					if (offset1 == 0)
						offset1 = 1;
					t = i;
					while (offset1 > 0) {
						t--;
						path.add(Map[t][off]);
						light = new TrafficLight(Map[t][off], TrafficDirection.SN, "light2");
						lightpath.add(Map[t][off].getLight2());
						offset1--;
					}
					if (Map[t][off].getxDirection() == TrafficDirection.WE) {
						//light = new TrafficLight(Map[t][off], TrafficDirection.WE, "light1");
						//lightpath.add(Map[t][off].getLight1());
						turns.add(Map[t][off].getLight1());
						while (off < totalStreets - 1) {
							off++;
							path.add(Map[t][off]);
							light = new TrafficLight(Map[t][off], TrafficDirection.WE, "light1");
							lightpath.add(Map[t][off].getLight1());
						}
						break;
					} else {
						//light = new TrafficLight(Map[t][off], TrafficDirection.EW, "light1");
						//lightpath.add(Map[t][off].getLight1());
						turns.add(Map[i][off].getLight1());
						while (off > 0) {
							off--;
							path.add(Map[t][off]);
							light = new TrafficLight(Map[t][off], TrafficDirection.EW, "light1");
							lightpath.add(Map[t][off].getLight1());
						}
						break;
					}
				}
			}	
			else if (x == TrafficDirection.EW && index[1] == totalStreets - 1)

			{
				TrafficLight light = new TrafficLight(start, TrafficDirection.EW, "light1");
				lightpath.add(start.getLight1());
				offset = ran.nextInt(totalStreets); // remove -1
				end = j + offset;
				off = j;
				while (flag == false) {
					offset = ran.nextInt(totalStreets); // remove -1 
					end = j - offset;
					off = j;
					ArrayList<Intersection> adj_element = Map[end][j].getAdjIntersections();
					adjnum = adj_element.size();
					if (adjnum == 0) {
						flag = false;
					} else if (i == totalAvenues - 1 && Map[end][j].getxDirection() == TrafficDirection.NS) {
						flag = false;
					} else {
						flag = true;
					}
				}

				while (off > end) {
					off--;
					path.add(Map[i][off]);
					light = new TrafficLight(Map[i][off], TrafficDirection.EW, "light1");
					lightpath.add(Map[i][off].getLight1());
				}
				if(GRID_DEBUG)
					System.out.println("the first turn is " + i + off);

				if (Map[i][off].getyDirection() == TrafficDirection.NS) {
				//	light = new TrafficLight(Map[i][off], TrafficDirection.NS, "light2");
				//	lightpath.add(Map[i][off].getLight2());
					turns.add(Map[i][off].getLight2());
					offset1 = rand.nextInt(totalAvenues - i ) ;
					// remove -1 
					if (offset1 == 0)
						offset1 = 1;
					t = i;
					while (offset1 > 0) {
						t++;
						path.add(Map[t][off]);
						light = new TrafficLight(Map[t][off], TrafficDirection.NS, "light2");
						lightpath.add(Map[t][off].getLight2());
						offset1--;
					}
					if (Map[t][off].getxDirection() == TrafficDirection.WE) {
						//light = new TrafficLight(Map[t][off], TrafficDirection.WE, "light1");
						//lightpath.add(Map[t][off].getLight1());
						turns.add(Map[t][off].getLight1());
						while (off < totalStreets - 1) {
							off++;
							path.add(Map[t][off]);
							light = new TrafficLight(Map[t][off], TrafficDirection.WE, "light1");
							lightpath.add(Map[t][off].getLight1());
						}
						break;
					} else {
					//	light = new TrafficLight(Map[t][off], TrafficDirection.EW, "light1");
					//	lightpath.add(Map[t][off].getLight1());
						turns.add(Map[t][off].getLight1());
						while (off > 0) {
							off--;
							path.add(Map[t][off]);
							light = new TrafficLight(Map[t][off], TrafficDirection.EW, "light1");
							lightpath.add(Map[t][off].getLight1());
						}
						break;
					}
				} else { // System.out.println("i" +i );

					//light = new TrafficLight(Map[i][off], TrafficDirection.SN, "light2");
					//lightpath.add(Map[i][off].getLight2());
					turns.add(Map[i][off].getLight2());
					offset1 = ran.nextInt(i) ;
					// System.out.println("offset1 is "+offset1);
					if (offset1 == 0)
						offset1 = 1;
					t = i;
					while (offset1 > 0) {
						t--;
						path.add(Map[t][off]);
						light = new TrafficLight(Map[t][off], TrafficDirection.NS, "light2");
						lightpath.add(Map[t][off].getLight2());
						offset1--;
					}
					if (Map[t][off].getxDirection() == TrafficDirection.WE) {
						//light = new TrafficLight(Map[t][off], TrafficDirection.WE, "light1");
						//lightpath.add(Map[t][off].getLight1());
						turns.add(Map[t][off].getLight1());
						while (off < totalStreets - 1) {
							off++;
							path.add(Map[t][off]);
							light = new TrafficLight(Map[t][off], TrafficDirection.WE, "light1");
							lightpath.add(Map[t][off].getLight1());
						}
						break;
					} else {
					//	light = new TrafficLight(Map[t][off], TrafficDirection.EW, "light1");
					//	lightpath.add(Map[t][off].getLight1());
						turns.add(Map[t][off].getLight1());
						while (off > 0) {
							off--;
							path.add(Map[t][off]);
							light = new TrafficLight(Map[t][off], TrafficDirection.EW, "light1");
							lightpath.add(Map[t][off].getLight1());

						}
						break;
					}
				}

			}
			// break;
		}

		// if(GRID_DEBUG)
		// for(Intersection item:path)
		// { System.out.println("node [" + item.getIndex()+"]"); }
		return lightpath;
	}
}
