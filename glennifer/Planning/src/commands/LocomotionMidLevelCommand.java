package commands;

/**
 * Created by Steven L. on 11/11/2016.
 */
public class LocomotionMidLevelCommand extends MidLevelCommand{

    // FIELDS
	
	//Float used for the CONFIGURE command
	//Should only be 0 to 1
	private float power;		
	
	//Enum used for CONFIGURE command
	private LocomotionConfigurationEnum configurationMode; 
		
	
	//Enum used if the command is CONFIGURE
	public enum LocomotionConfigurationEnum {

    	STRAIGHT, //The robot can only move forward or backward
		TURN, //The robot can only turn in place
		STRAFE //The robot can only strafe side to side
    }
	
	
	

    public LocomotionMidLevelCommand(float speed, float timeout, MidLevelCommandEnum command){
        super(speed, timeout);
        this.command = command;
    }
    
 // GET/SET

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        if(power > 0f && power < 1f){
        	this.power = power;
        }
    }


    public void setCommand(LocomotionConfigurationEnum configurationMode) {
        this.configurationMode = configurationMode;
    }

    //
}
