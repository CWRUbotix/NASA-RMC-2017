
datestring=$(date +%Y_%m_%d_%H_%M_%S)
cd /home/cwrubotix
mkdir -p "logs/$datestring"

cd /home/cwrubotix/workspace/NASA-RMC-2017/

# start the things
cd glennifer/HCI
nohup mvn exec:java -Dexec.mainClass="com.cwrubotix.glennifer.hci.ModuleMain" &> "/home/cwrubotix/logs/$datestring/ModuleMainOutput.log" &
cd ..

cd robot_state
nohup mvn exec:java -Dexec.mainClass="com.cwrubotix.glennifer.robot_state.StateModule" &> "/home/cwrubotix/logs/$datestring/StateModuleOutput.log" &
cd ..

cd autodrill
nohup mvn exec:java -Dexec.mainClass="com.cwrubotix.glennifer.autodrill.AutoDrillModule" &> "/home/cwrubotix/logs/$datestring/AutoDrillModuleOutput.log" &
cd ..

cd motor_dispatch/src/python
nohup python3 locomotion.py &> "/home/cwrubotix/logs/$datestring/LocomotionPyOutput.log" &
cd ../../../..

cd client-cameras
nohup python client-cam-send.py &> "/home/cwrubotix/logs/$datestring/CameraSendPyOutput.log" &
cd ..

cd ..
