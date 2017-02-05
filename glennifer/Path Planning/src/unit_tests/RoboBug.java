package unit_tests;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import algorithms.AStar;
import info.gridworld.actor.Actor;
import info.gridworld.actor.Bug;
import info.gridworld.actor.Flower;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

public class RoboBug extends Bug {

	// This arraylist if for maybe reducing the amount of resources this class
	// takes by recreating the obstacle list every time
	// ArrayList<Integer> occupied_coords;

	int targetX, targetY; // the coordinates of the target space
	ArrayList<int[]> path;
	int obstacle_counter;
	int act_counter;

	/**
	 * This is an demo for AStar path finding that works for a grid in which
	 * obstacles do not move. It searches for the red TargetFlower
	 */
	public RoboBug() {
		setColor(Color.YELLOW);
		obstacle_counter = -1;
		act_counter = 0;
	}

	/**
	 * Rotates the bug towards a desired angle
	 * @param direction
	 */
	public void turn(int direction) {
		setDirection(direction);
	}

	/**
	 * Calculates the path when first called, then moves along the plotted path.
	 */
	@Override
	public void act() {
		// int num_obstacles = countObstacles();
		// if (num_obstacles != obstacle_counter){
		// num_obstacles = obstacle_counter;
		// findTarget();
		// aStarPath();
		// }
		if (act_counter == 0) {
			findTarget();
			aStarPath();
			act_counter++;
		}

		if (path.size() > 0) {
			super.moveTo(new Location(path.get(0)[0], path.get(0)[1]));
			path.remove(0);
		}
		else{
			act_counter=0;
			Random randy = new Random();
			super.setColor(Color.getHSBColor(randy.nextFloat(), randy.nextFloat(), randy.nextFloat()));
		}
	}

	public void findTarget() {
		Grid<Actor> gr = getGrid();
		if (gr == null)
			return;

		// int targetCounter = 0;
		ArrayList<Location> occupied = gr.getOccupiedLocations();
		for (Location loc : occupied) {
			if (gr.get(loc) instanceof TargetFlower) {
				targetX = loc.getRow();
				targetY = loc.getCol();
			}
		}
	}

	/**
	 * This is for counting the number of obstacles in the grid.
	 * @return the number of obstacles
	 */
	public int countObstacles() {
		Grid<Actor> gr = getGrid();
		if (gr == null) {
			System.err.println("We have problema");
			return -1;
		}

		ArrayList<Location> occupied = gr.getOccupiedLocations();
		return occupied.size();
	}

	/**
	 * Sets the direction towards a neighbor cell. Does not work if cell is not
	 * adjacent.
	 */
	public void setDirectionTowards(Location loc) {
		// The change in x, and change in y
		int dx = loc.getRow() - getLocation().getRow();
		int dy = loc.getCol() - getLocation().getCol();

		// find the angle
		// int angle = (int) Math.round(Math.atan(dy / dx));

		// if positive change in x
		if (dx == 1) {
			if (dy == 1)
				setDirection(Location.NORTHEAST);
			else if (dy == 0)
				setDirection(Location.EAST);
			else if (dy == -1)
				setDirection(Location.SOUTHEAST);
			else
				System.err.println("Somehow the next location in the path is further than 1 space away. (dx == 1)");
		} else if (dx == 0) {
			if (dy == 1)
				setDirection(Location.NORTH);
			else if (dy == 0)
				System.out.println("We're here?");
			else if (dy == -1)
				setDirection(Location.SOUTH);
			else
				System.err.println("Somehow the next location in the path is further than 1 space away. (dx == 0)");

		} else if (dx == -1) {
			if (dy == 1)
				setDirection(Location.NORTHWEST);
			else if (dy == 0)
				setDirection(Location.WEST);
			else if (dy == -1)
				setDirection(Location.SOUTHWEST);
			else
				System.err.println("Somehow the next location in the path is further than 1 space away. (dx == -1)");
		}

	}

	/**
	 * Plots a path based on the AStar algorithm
	 */
	public void aStarPath() {
		Grid<Actor> gr = getGrid();
		if (gr == null)
			return;

		// ArrayList of all the occupied locations on the grid
		ArrayList<Location> occupied = gr.getOccupiedLocations();
		// if the occupied location is a flower or this bug
		for (int i = 0; i < occupied.size(); i++) {
			if (gr.get(occupied.get(i)) instanceof Flower || occupied.get(i).equals(getLocation())) {
				occupied.remove(i);
				i--;
			}
		}

		// how many obstacles on the field
		obstacle_counter = occupied.size();

		// Adds the coordinates of all the occupied spaces
		int[][] occupied_coords = new int[occupied.size()][2];
		int counter = 0;
		for (Location a : occupied) {
			occupied_coords[counter][0] = a.getRow();
			occupied_coords[counter][1] = a.getCol();
			counter++;
		}

		// sets the path to the algorithm's path
		path = AStar.planPath(gr.getNumRows(), gr.getNumCols(), getLocation().getRow(), getLocation().getCol(), targetX,
				targetY, occupied_coords);
	}
}
