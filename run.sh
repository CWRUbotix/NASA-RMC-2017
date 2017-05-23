
datestring=$(date +%Y_%m_%d_%H_%M_%S)
cd /home/cwrubotix
mkdir -p "logs/$datestring"

cd /home/cwrubotix/workspace/NASA-RMC-2017/

# start the things
#cd glennifer/HCI
#nohup java -cp target/hci-1.0-SNAPSHOT.jar com.cwrubotix.glennifer.hci.ModuleMain &> "/home/cwrubotix/logs/$datestring/ModuleMainOutput.log" &
#cd ..

cd glennifer
cd robot_state
nohup java -cp target/robot_state-1.0-SNAPSHOT.jar com.cwrubotix.glennifer.robot_state.StateModule &> "/home/cwrubotix/logs/$datestring/StateModuleOutput.log" &
cd ..

sleep 2

cd autodrill
nohup java -cp target/autodrill-1.0-SNAPSHOT.jar com.cwrubotix.glennifer.autodrill.AutoDrillModule &> "/home/cwrubotix/logs/$datestring/AutoDrillModuleOutput.log" &
cd ..

cd motor_dispatch/src/python
nohup python3 locomotion.py &> "/home/cwrubotix/logs/$datestring/LocomotionPyOutput.log" &
cd ../../../..

cd client-cameras
nohup python client-cam-send.py &> "/home/cwrubotix/logs/$datestring/CameraSendPyOutput.log" &
cd ..

cd ..
