#define ID_LBM  0
#define ID_RBM  1
#define ID_LFM  2
#define ID_RFM  3

#include "RoboClaw.h"
#include "math.h"

#define COMMAND_READ_SENSORS 0x01
#define COMMAND_SET_OUTPUTS 0x02
#define COMMAND_HCI_TEST    0x5A

#define RESPONSE_HCI_TEST   0xA5

#define ADDRESS_RC_0 0x80
#define ADDRESS_RC_1 0x81
#define ADDRESS_RC_2 0x82
#define ADDRESS_RC_3 0x83

#define MAX_SENSORS 256

struct message {
  byte command;
  byte len;
  byte* data;
};

int state = 0;

int num_sensors = 0;
int sensor_ids[MAX_SENSORS];
int sensor_data[MAX_SENSORS];

RoboClaw roboclaw(&Serial1,10000);

void setup() {
  roboclaw.begin(38400);
  SerialUSB.begin(9600);
}

void loop() {
  while(SerialUSB.available()) {
    message ms = hciRead();
    execute(ms);
  }
}

void execute(message m) {
  byte response_data[MAX_SENSORS * 4]; // max-sized response buffer
  switch(m.command) {
    case COMMAND_HCI_TEST:
      SerialUSB.write(RESPONSE_HCI_TEST);
      break;
    case COMMAND_READ_SENSORS:
      for(int i = 0; i < m.len/2; i++) {
        short id =  (m.data[i*2+0] << 8) | m.data[i*2+1];
        
        //uint8_t status;
        //bool valid;
        // short val = (short)roboclaw.ReadEncM1(id, &status, &valid); // This line causes everything to fail
        //if (!valid){
        //  val = 0; // TODO: handle this
        //}
        short val = 99;
        
        
        response_data[i*4+0] = (byte)(id >> 8);
        response_data[i*4+1] = (byte)id;
        response_data[i*4+2] = (byte)(val >> 8);
        response_data[i*4+3] = (byte)val;

        message resp;
        resp.command = COMMAND_READ_SENSORS;
        resp.len = (m.len/2) * 4;
        resp.data = response_data;
        hciSend(resp);
      }
      break;
    case COMMAND_SET_OUTPUTS:
      for(int i = 0; i < m.len/4; i++) {
        short id = (m.data[i*4+0] << 8)|m.data[i*4+1];
        short out = (m.data[i*4+2] << 8)|m.data[i*4+3];
        setActuator(id, out);
      }
      message resp;
      resp.command = COMMAND_SET_OUTPUTS;
      resp.len = 1;
      response_data[0] = 0;
      resp.data = response_data;
      hciSend(resp);
  }
}
void readSensors() {
  
}

void setActuator(short ID, short val) {
  // Initialize variables for easier switching
  // Direction of movement (true is forward)
  bool dir = (val > 0);
  // Absolute value (doesn't work for -128, which should be illegal)
  if (val < 0) {
    val = -val;
  }
  // True if roboclaw controller
  bool rc = false;
  // True if sabertooth controller
  bool st = false;
  // Address
  byte addr = 0;
  // Channel on motor controllers
  byte chan = 0;
  switch(ID) {
    case ID_LBM:
      rc = true;
      addr = ADDRESS_RC_0;
      chan = 1;
      break;
    case ID_RBM:
      rc = true;
      addr = ADDRESS_RC_0;
      chan = 2;
      break;
    case ID_LFM:
      rc = true;
      addr = ADDRESS_RC_1;
      chan = 1;
      break;
    case ID_RFM:
      rc = true;
      addr = ADDRESS_RC_1;
      chan = 2;
      break;
  }
  // Handle roboclaw controllers
  if(rc) {
    // Separate directions
    if(dir) {
      // Separate channels
      if(chan == 1) {
        roboclaw.ForwardM1(addr,val);
      } else {
        roboclaw.ForwardM2(addr,val);
      }
    } else {
      // Separate channels
      if(chan == 1) {
        roboclaw.BackwardM1(addr,val);
      } else {
        roboclaw.BackwardM2(addr,val);
      }
    }
  }
}

bool hciSend(message m) {
  if(SerialUSB.write(m.command) != 1) {
    return false;
  }
  if(SerialUSB.write(m.len) != 1) {
    return false;
  }
  if(SerialUSB.write(m.data,m.len) == m.len) {
    return true;
  }
  return false;
}

message hciRead() {
  byte head[2];
  SerialUSB.readBytes(head,2);
  byte data[head[1]];
  SerialUSB.readBytes(data,head[1]);
  message out;
  out.command = head[0];
  out.len = head[1];
  out.data = data;
  return out;
}

