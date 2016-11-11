public class MotorLayer {
  
  // what should be the return type
  // where the configuration file should be
  public void receive(Object command, Object state) {
    switch (state) {
      // locomotion state
      case Locomotion: { 
        // commands in locomotion states
        switch (commmand) {
          case moveStraight:
            return 
            break;
            
          case strafe:
            return 
            break;
          
          case turn:
            return 
            break;
          
          case configure:
            return;
            break;
            
        }
      }
      
      // excavation state
      case Excavation: {
        // commands in excavation state
        switch (command) {
          // commands in excavation state
          case arm:
            return;
            break;
            
          case translation:
            return;
            break;   
            
          case bucket:
            return;
            break;
        }
      }
      
      // deposition state
      case Deposition: {
        // commands in deposition state
        switch(command) {
          case dump:
            return;
            break;
          
          case conveyor:
            return;
            break;
        }
      }
    }
  }
  
  /* future commands definition */
  
}