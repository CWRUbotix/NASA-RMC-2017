package commands;

/**
 * This is the temporary high level command for Glennifer's locomotion
 * @author Shota
 *
 */
public class LocoHLC extends HighLevelCommand {
	
	/**
	 * The x coordinate of the destination point
	 */
	float x;
	
	/**
	 * The y coordinate of the destination point
	 */
	float y;
	
	/**
	 * The z coordinate of the destination point
	 */
	float z;

	/**
	 * Sets the coordinates of the destination point
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param z The z-coordinate
	 */
	public LocoHLC(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
}
