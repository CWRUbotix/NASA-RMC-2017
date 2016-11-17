package unit_tests;

import java.awt.Color;

import info.gridworld.actor.Flower;

public class TargetFlower extends Flower {
	
	public TargetFlower(){
		super(Color.RED);
	}
	
	@Override
	public void act(){
		//TargetFlower does nothing except be red
	}
}
