#include "RoboClaw.h"
#include "math.h"

#define ID_LBM  0
#define ID_RBM  1
#define ID_LFM  2
#define ID_RFM  3

#define COMMAND_READ_SENSORS (0x01)
#define COMMAND_SET_OUTPUTS (0x02)
#define COMMAND_HCI_TEST    (0x5A)

#define RESPONSE_HCI_TEST   (0xA5)

#define ADDRESS_RC_0 (0x80)
#define ADDRESS_RC_1 (0x81)
#define ADDRESS_RC_2 (0x82)
#define ADDRESS_RC_3 (0x83)

#define CMD_HEADER_SIZE (2)
#define RPY_HEADER_SIZE (2)

#define FAULT_T uint16_t
#define NO_FAULT (1)
#define FAULT_FAILED_WRITE (2)
#define FAULT_INCOMPLETE_HEADER (3)
#define FAULT_CORRUPTED_HEADER (4)
#define FAULT_INCOMPLETE_BODY (5)
#define FAULT_CORRUPTED_BODY (6)

RoboClaw roboclaw(&Serial1,10000);

void setup() {
  roboclaw.begin(38400);
  SerialUSB.begin(9600);
  roboclaw.SetConfig(ADDRESS_RC_0, 0x8063);
  roboclaw.SetConfig(ADDRESS_RC_1, 0x8163);
  roboclaw.SetConfig(ADDRESS_RC_2, 0x8263);
  roboclaw.SetConfig(ADDRESS_RC_3, 0x8363);
  roboclaw.SetM1EncoderMode(ADDRESS_RC_1, 0x81);
  roboclaw.WriteNVM(ADDRESS_RC_0);
  roboclaw.WriteNVM(ADDRESS_RC_1);
  roboclaw.WriteNVM(ADDRESS_RC_2);
  roboclaw.WriteNVM(ADDRESS_RC_3);
}

void loop() {
  while (true) {
    FAULT_T retfault;
    byte cmd[256];
    hciWait();
    retfault = hciRead(cmd);
    if (retfault != NO_FAULT) {
      // Enter sync mode
      // When done:
      continue;
    }
    // cmd is valid
    execute(cmd);
  }
}

void execute(byte cmd[]) {
  byte rpy[256]; // max-sized response buffer
  uint8_t type = cmd_type(cmd);
  uint8_t num_sensors_requested;
  uint8_t num_motors_requested;
  FAULT_T retfault;
  switch(type) {
    case COMMAND_HCI_TEST:
      SerialUSB.write(RESPONSE_HCI_TEST);
      break;
    case COMMAND_READ_SENSORS:
      rpy_init(rpy, type);
      num_sensors_requested = cmd_sense_num_sensors(cmd);
      for(int i = 0; i < num_sensors_requested; i++) {
        uint16_t id =  cmd_sense_sensor_id(cmd, i);
        
        uint8_t status;
        bool valid = true;
        int32_t val32 = roboclaw.ReadEncM1(ADDRESS_RC_1, &status, &valid);
        int16_t val = (int16_t)val32;
        if (!valid){
          val = 0x8000; // Error code
        }
        bool overflow = rpy_sense_add_sensor(rpy, id, val);
        if (overflow) {
          // do nothing
        }
      }
      rpy_finalize(rpy);
      retfault = hciWrite(rpy);
      if (retfault != NO_FAULT) {
        // TODO: enter sync mode
      }
      break;
    case COMMAND_SET_OUTPUTS:
      rpy_init(rpy, type);
      num_motors_requested = cmd_ctl_num_motors(cmd);
      for(int i = 0; i < num_motors_requested; i++) {
        uint16_t id = cmd_ctl_motor_id(cmd, i);
        int16_t val = cmd_ctl_motor_val(cmd, i);
        setActuator(id, val);
      }
      rpy_finalize(rpy);
      retfault = hciWrite(rpy);
      if (retfault != NO_FAULT) {
        // TOCO: enter sync mode
      }
      break;
  }
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

void hciWait() {
  while(!SerialUSB.available()) {}
}

FAULT_T hciWrite(byte rpy[]) {
  uint8_t retval;
  uint8_t len = rpy_len(rpy);
  retval = SerialUSB.write(rpy,len+2);
  if (retval != len+2) {
    return FAULT_FAILED_WRITE;
  }
  return NO_FAULT;
}

FAULT_T hciRead(byte cmd[]) {
  uint8_t retval;
  retval = SerialUSB.readBytes(cmd, CMD_HEADER_SIZE);
  if (retval != CMD_HEADER_SIZE) {
    return FAULT_INCOMPLETE_HEADER;
  }
  bool valid = cmd_check_head(cmd);
  if (!valid) {
    return FAULT_CORRUPTED_HEADER;
  }
  uint8_t len = cmd_len(cmd);
  retval = SerialUSB.readBytes(cmd + CMD_HEADER_SIZE, len);
  if (retval != len) {
    return FAULT_INCOMPLETE_BODY;
  }
  valid = cmd_check_body(cmd);
  if (!valid) {
    return FAULT_CORRUPTED_BODY;
  }
  return NO_FAULT;
}

// Precondition: every message array has length at least 256

bool cmd_check_head(byte cmd[]) {
  return true;
}

uint8_t cmd_type(byte cmd[]) {
  return cmd[0];
}

uint8_t cmd_len(byte cmd[]) {
  return cmd[1];
}

bool cmd_check_body(byte cmd[]) {
  return true;
}

uint8_t cmd_sense_num_sensors(byte cmd[]) {
  return cmd_len(cmd)/2;
}

uint16_t cmd_sense_sensor_id(byte cmd[], uint8_t i) {
  // i unchecked
  return ((uint16_t)cmd[CMD_HEADER_SIZE + 2*i + 0] << 8) | cmd[CMD_HEADER_SIZE + 2*i + 1];
}

uint8_t cmd_ctl_num_motors(byte cmd[]) {
  return cmd_len(cmd)/4;
}

uint16_t cmd_ctl_motor_id(byte cmd[], uint8_t i) {
  // i unchecked
  return ((uint16_t)cmd[CMD_HEADER_SIZE + 4*i + 0] << 8) | cmd[CMD_HEADER_SIZE + 4*i + 1];
}

int16_t cmd_ctl_motor_val(byte cmd[], uint8_t i) {
  // i unchecked
  return ((int16_t)cmd[CMD_HEADER_SIZE + 4*i + 2] << 8) | cmd[CMD_HEADER_SIZE + 4*i + 3];
}

void rpy_init(byte rpy[], uint8_t type) {
  rpy[0] = type;
  rpy[1] = 0; // len
}

uint8_t rpy_len(byte rpy[]) {
  return rpy[1];
}

void rpy_set_len(byte rpy[], uint8_t new_len) {
  rpy[1] = new_len;
}

bool rpy_sense_add_sensor(byte rpy[], uint16_t id, int16_t val) {
  // size unchecked
  uint8_t len = rpy_len(rpy);
  if (len >= 252) {
    return true;
  }
  byte *rpy_id_high_ptr = rpy + RPY_HEADER_SIZE + len + 0;
  byte *rpy_id_low_ptr = rpy + RPY_HEADER_SIZE + len + 1;
  byte *rpy_val_high_ptr = rpy + RPY_HEADER_SIZE + len + 2;
  byte *rpy_val_low_ptr = rpy + RPY_HEADER_SIZE + len + 3;
  *rpy_id_high_ptr = (byte)(id >> 8);
  *rpy_id_low_ptr = (byte)id;
  *rpy_val_high_ptr = (byte)((unsigned)val >> 8);
  *rpy_val_low_ptr = (byte)val;
  len += 4;
  rpy_set_len(rpy, len);
  return false;
}

bool rpy_ctl_add_motor(byte rpy[], uint16_t id, int16_t val) {
  // size unchecked
  uint8_t len = rpy_len(rpy);
  if (len >= 252) {
    return true;
  }
  byte *rpy_id_high_ptr = rpy + RPY_HEADER_SIZE + len + 0;
  byte *rpy_id_low_ptr = rpy + RPY_HEADER_SIZE + len + 1;
  byte *rpy_val_high_ptr = rpy + RPY_HEADER_SIZE + len + 2;
  byte *rpy_val_low_ptr = rpy + RPY_HEADER_SIZE + len + 3;
  *rpy_id_high_ptr = (byte)(id >> 8);
  *rpy_id_low_ptr = (byte)id;
  *rpy_val_high_ptr = (byte)((unsigned)val >> 8);
  *rpy_val_low_ptr = (byte)val;
  len += 4;
  rpy_set_len(rpy, len);
  return false;
}

void rpy_finalize(byte rpy[]) {}


