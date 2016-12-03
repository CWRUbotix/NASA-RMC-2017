package commands;

public class ExcHLC extends HighLevelCommand {
	
	/**
	 * The desired digging depth of the excavation system
	 */
	float depth;
	
	/**
	 * The desired duration of the dig
	 */
	float duration;
	
	/**
	 * The desired speed of the bucket conveyor
	 */
	float beltSpeed;
	
	/**
	 * The desired amount of regolith excavated
	 */
	float amount;
	
	/**
	 * The desired digging angle
	 */
	float armAngle;
	
	/**
	 * The desired translation length of the excavation system.
	 */
	float translation;
	
	/**
	 * Creates a high level excavation command with a specified
	 * depth, belt speed, amount dug / duration of dig. This command will automatically
	 * choose the steepest angle to dig.
	 * @param depth
	 * @param beltSpeed
	 * @param amount Set this value as -1 if you want no specified amount (duration already specified)
	 * @param duration Set this value as -1 if you want no specified duration (amount already specified)
	 */
	public ExcHLC(float depth, float beltSpeed, float amount, float duration){
		this.depth = depth;
		this.beltSpeed = beltSpeed;
		this.amount = amount;
		this.duration = duration;
	}
	
	/**
	 * Creates a high level excavation command with a specified
	 * depth, belt speed, amount dug / duration of dig, arm Angle and arm 
	 * translation length.
	 * @param depth
	 * @param amount Set this value as -1 if you want no specified amount (duration already specified)
	 * @param duration Set this value as -1 if you want no specified duration (amount already specified)
	 * @param armAngle The angle the excavation system is to be set at
	 * @param translation The translation
	 */
	public ExcHLC(float depth, float beltSpeed, float amount, float duration, float armAngle, float translation){
		this.depth = depth;
		this.beltSpeed = beltSpeed;
		this.amount = amount;
		this.duration = duration;
		this.armAngle = armAngle;
		this.translation = translation;
	}
}
