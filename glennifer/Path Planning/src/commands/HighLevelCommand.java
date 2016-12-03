package commands;

import java.util.List;

/**
 * This are the current data values needing to be set in the high level command.
 * @author Shota
 *
 */
public abstract class HighLevelCommand {

	/**
	 * This field serves as the integer portion of the time stamp
	 */
	int intTime;
	
	/**
	 * This field serves as the fractional portion of the time stamp
	 */
	float fracTime;
	
	/**
	 * This field serves as the Identification number of the command,
	 * and should be a nonnegative integer.
	 */
	int id;

	public int getIntTime() {
		return intTime;
	}

	public void setIntTime(int intTime) {
		this.intTime = intTime;
	}

	public float getFracTime() {
		return fracTime;
	}

	public void setFracTime(float fracTime) {
		this.fracTime = fracTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	//-------------Not to use-----------------
	
//	/**
//	 * This list is for containing all of the data members of the commmand
//	 * for easy access.
//	 */
//	List<Object> dataMembers;
//	
//	public abstract List<Object> getDataMembers();
}
