#include <Sabertooth.h>
#include <RoboClaw.h>
#include <math.h>

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
#define FAULT_LOST_ROBOCLAW (6)

enum SensorHardware {
  SH_NONE,
  SH_RC_POT,
  SH_RC_ENC,
  SH_PIN_LIMIT
};

typedef struct SensorInfo {
  SensorHardware hardware;
  uint8_t addr; // When hardware = SH_RC_*
  bool whichMotor; // When hardware = SH_RC_*
  uint8_t whichPin; // When hardware = SH_PIN_*
} SensorInfo;

enum MotorHardware {
  MH_NONE,
  MH_RC_PWM,
  MH_RC_VEL,
  MH_RC_POS,
  MH_ST_PWM
};

typedef struct MotorInfo {
  MotorHardware hardware;
  uint8_t addr;
  bool whichMotor;
  float kp; // When hardware = MH_RC_POS or MC_RC_VEL
  float ki; // When hardware = MH_RC_POS or MC_RC_VEL
  float kd; // When hardware = MH_RC_POS or MC_RC_VEL
  uint32_t qpps; // When hardware = MH_RC_POS or MC_RC_VEL
  uint32_t deadband; // When hardware = MH_RC_POS
  uint32_t minpos; // When hardware = MH_RC_POS
  uint32_t maxpos; // When hardware = MH_RC_POS
  uint32_t accel;
} MotorInfo;

SensorInfo sensor_infos[256]; // All initialized to SH_NONE
MotorInfo motor_infos[256]; // All initialized to MH_NONE

RoboClaw roboclaw(&Serial1,10000);
Sabertooth sabretooth[4] = {
  Sabertooth(0x80, Serial2),
  Sabertooth(0x81, Serial2),
  Sabertooth(0x82, Serial2),
  Sabertooth(0x83, Serial2),
};

void setup() {
  sensor_infos[0].hardware = SH_RC_POT;
  sensor_infos[0].addr = ADDRESS_RC_3;
  sensor_infos[0].whichMotor = 0;
  
  sensor_infos[1].hardware = SH_PIN_LIMIT;
  sensor_infos[1].whichPin = 13;
  
  sensor_infos[2].hardware = SH_PIN_LIMIT;
  sensor_infos[2].whichPin = 14;

  /*
  motor_infos[2].hardware = MH_RC_POS;
  motor_infos[2].addr = ADDRESS_RC_3;
  motor_infos[2].whichMotor = 0;
  motor_infos[2].kp = 31512.70535;
  motor_infos[2].ki = 23.57707;
  motor_infos[2].kd = 7019890.76290;
  motor_infos[2].qpps = 330;
  motor_infos[2].deadband = 10;
  motor_infos[2].minpos = 84;
  motor_infos[2].maxpos = 1676;
  motor_infos[2].accel = 9999999;
  */
  
  motor_infos[2].hardware = MH_ST_PWM;
  motor_infos[2].addr = 0;
  motor_infos[2].whichMotor = 1;

  setup_comms();
  setup_sabretooth();
  setup_roboclaw();
  configure_sensors();
  configure_motors();
}

void setup_comms() {
  SerialUSB.begin(9600);
}

void setup_sabretooth() {
  Serial2.begin(9600);
}

FAULT_T setup_roboclaw() {
  roboclaw.begin(38400);
  bool success;
  success = roboclaw.SetConfig(ADDRESS_RC_0, 0x8063);
  if (!success) {
    return FAULT_LOST_ROBOCLAW;
  }
  success = roboclaw.SetConfig(ADDRESS_RC_1, 0x8163);
  if (!success) {
    return FAULT_LOST_ROBOCLAW;
  }
  success = roboclaw.SetConfig(ADDRESS_RC_2, 0x8263);
  if (!success) {
    return FAULT_LOST_ROBOCLAW;
  }
  success = roboclaw.SetConfig(ADDRESS_RC_3, 0x8363);
  if (!success) {
    return FAULT_LOST_ROBOCLAW;
  }
  
  success = roboclaw.WriteNVM(ADDRESS_RC_0);
  if (!success) {
    return FAULT_LOST_ROBOCLAW;
  }
  success = roboclaw.WriteNVM(ADDRESS_RC_1);
  if (!success) {
    return FAULT_LOST_ROBOCLAW;
  }
  success = roboclaw.WriteNVM(ADDRESS_RC_2);
  if (!success) {
    return FAULT_LOST_ROBOCLAW;
  }
  success = roboclaw.WriteNVM(ADDRESS_RC_3);
  if (!success) {
    return FAULT_LOST_ROBOCLAW;
  }
  return NO_FAULT;
}

FAULT_T configure_sensors() {
  bool success;
  for (int i = 0; i < 256; i++) {
    SensorInfo sensor_info = sensor_infos[i];
    switch (sensor_info.hardware) {
    case SH_RC_POT:
      if (sensor_info.whichMotor) {
        success = roboclaw.SetM2EncoderMode(sensor_info.addr, 0x81);
      } else {
        success = roboclaw.SetM1EncoderMode(sensor_info.addr, 0x81);
      }
      if (!success) {
        return FAULT_LOST_ROBOCLAW;
      }
      break;
    case SH_RC_ENC:
      if (sensor_info.whichMotor) {
        success = roboclaw.SetM2EncoderMode(sensor_info.addr, 0x80);
      } else {
        success = roboclaw.SetM1EncoderMode(sensor_info.addr, 0x80);
      }
      if (!success) {
        return FAULT_LOST_ROBOCLAW;
      }
      break;
    case SH_PIN_LIMIT:
      // TODO
      break;
    default:
      break;
    }
  }
  return NO_FAULT;
}

FAULT_T configure_motors() {
  bool success;
  for (int i = 0; i < 256; i++) {
    MotorInfo motor_info = motor_infos[i];
    switch (motor_info.hardware) {
    case MH_RC_PWM:
      // Nothing to do, default config
      break;
    case MH_RC_VEL:
      if (motor_info.whichMotor) {
        success = roboclaw.SetM2VelocityPID(
          motor_info.addr,
          motor_info.kp,
          motor_info.ki,
          motor_info.kd,
          motor_info.qpps);
      } else {
        success = roboclaw.SetM1VelocityPID(
          motor_info.addr,
          motor_info.kp,
          motor_info.ki,
          motor_info.kd,
          motor_info.qpps);
      }
      if (!success) {
        return FAULT_LOST_ROBOCLAW;
      }
      break;
    case MH_RC_POS:
      if (motor_info.whichMotor) {
        success = roboclaw.SetM2PositionPID(
          motor_info.addr,
          motor_info.kp,
          motor_info.ki,
          motor_info.kd,
          motor_info.qpps,
          motor_info.deadband,
          motor_info.minpos,
          motor_info.maxpos);
      } else {
        success = roboclaw.SetM1PositionPID(
          motor_info.addr,
          motor_info.kp,
          motor_info.ki,
          motor_info.kd,
          motor_info.qpps,
          motor_info.deadband,
          motor_info.minpos,
          motor_info.maxpos);
      }
      if (!success) {
        return FAULT_LOST_ROBOCLAW;
      }
      break;
    case MH_ST_PWM:
      // Nothing to do, default config
      break;
    default:
      break;
    }
  }
  return NO_FAULT;
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
        int16_t val;
        FAULT_T retfault = getSensor(id, &val);
        if (retfault != NO_FAULT) {
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

FAULT_T getSensor(uint16_t ID, int16_t *val) {
  SensorInfo sensor_info = sensor_infos[ID];
  uint8_t status;
  bool valid;
  int32_t val32;
  switch (sensor_info.hardware) {
  case SH_RC_POT:
    if (sensor_info.whichMotor) {
      val32 = roboclaw.ReadEncM2(sensor_info.addr, &status, &valid);
    } else {
      val32 = roboclaw.ReadEncM1(sensor_info.addr, &status, &valid);
    }
    if (!valid){
      return FAULT_LOST_ROBOCLAW;
    }
    *val = (int16_t)val32;
    break;
  case SH_RC_ENC:
    if (sensor_info.whichMotor) {
      val32 = roboclaw.ReadSpeedM2(sensor_info.addr, &status, &valid);
    } else {
      val32 = roboclaw.ReadSpeedM1(sensor_info.addr, &status, &valid);
    }
    if (!valid){
      return FAULT_LOST_ROBOCLAW;
    }
    *val = (int16_t)val32;
    break;
  case SH_PIN_LIMIT:
    // TODO
    break;
  default:
    break;
  }
  return NO_FAULT;
}

FAULT_T setActuator(uint16_t ID, int16_t val) {
  bool success;
  // Direction of movement (true is forward)
  bool dir = (val > 0);
  // Absolute value (doesn't work for -128, which should be illegal)
  int16_t mag;
  if (val < 0) {
    mag = -val;
  } else {
    mag = val;
  }
  MotorInfo motor_info = motor_infos[ID];
  switch (motor_info.hardware) {
  case MH_RC_PWM:
    if (motor_info.whichMotor) {
      success = roboclaw.DutyM2(motor_info.addr, val);
    } else {
      success = roboclaw.DutyM1(motor_info.addr, val);
    }
    break;
  case MH_RC_VEL:
    if (motor_info.whichMotor) {
      success = roboclaw.SpeedM2(motor_info.addr, val);
    } else {
      success = roboclaw.SpeedM1(motor_info.addr, val);
    }
    if (!success) {
      return FAULT_LOST_ROBOCLAW;
    }
    break;
  case MH_RC_POS:
    if (motor_info.whichMotor) {
      success = roboclaw.SpeedAccelDeccelPositionM2(
        motor_info.addr,
        motor_info.accel,
        motor_info.qpps,
        motor_info.accel,
        val,
        0);
    } else {
      success = roboclaw.SpeedAccelDeccelPositionM1(
        motor_info.addr,
        motor_info.accel,
        motor_info.qpps,
        motor_info.accel,
        val,
        0);
    }
    if (!success) {
      return FAULT_LOST_ROBOCLAW;
    }
    break;
  case MH_ST_PWM:
    sabretooth[motor_info.addr].motor(motor_info.whichMotor, val);
    break;
  default:
    break;
  }
  return NO_FAULT;
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


