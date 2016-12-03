package commands;

import java.util.List;

/**
 * This is the temporary high level command for controlling Glennifer's deposition
 * @author Shota
 *
 */
public class DepHLC extends HighLevelCommand {
	/**
	 * This field serves as the integer portion for the desired duration
	 * of the dump.
	 */
	int intDuration;

	/**
	 * This field serves as the fractional portion for the desired duration
	 * of the dump.
	 */
	float fracDuration;
	
	/**
	 * This field serves as desired amount left in bin
	 */
	float amount;

	/**
	 * Use this constructor if you wish to empty the bin
	 */
	public DepHLC(){
		this.amount = 0;
		this.fracDuration = -1;
		this.intDuration = -1;
	}
	
	/**
	 * Use this constructor if there is a desired remaining amount,
	 * but unspecified time
	 * @param amount Desired dump amount
	 */
	public DepHLC(float amount){
		this.amount = amount;
		this.fracDuration = -1;
		this.intDuration = -1;
	}
	
	/**
	 * Use this constructor if there is a desired dump duration,
	 * but unspecified amount
	 * @param intDuration Integer portion of the duration
	 * @param fracDuration Fractional portion of the duration
	 */
	public DepHLC(int intDuration, float fracDuration){
		this.intDuration = intDuration;
		this.fracDuration = fracDuration;
		this.amount = -1;
	}
	
	/**
	 * This constructor cannot exist
	 * @param intDuration Integer portion of the duration
	 * @param fracDuration Fractional portion of the duration
	 * @param amount Desired dump amount
	 */
//	public DepHLC(int intDuration, float fracDuration, float amount){
//		this.intDuration = intDuration;
//		this.fracDuration = fracDuration;
//		this.amount = amount;
//	}
//	
	
	public int getIntDuration() {
		return intDuration;
	}

	public void setIntDuration(int intDuration) {
		this.intDuration = intDuration;
	}

	public float getFracDuration() {
		return fracDuration;
	}

	public void setFracDuration(float fracDuration) {
		this.fracDuration = fracDuration;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

}
