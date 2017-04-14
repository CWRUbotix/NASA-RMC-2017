
datestring=$(date +%Y_%m_%d_%H_%M_%S)
mkdir -p "/home/glennifer/logs/$datestring"

cd /home/cwrubotix/workspace/NASA-RMC-2017/

#build protobuf
./build-pb.sh

# copy config
cp -r config glennifer/HCI/
cp -r config glennifer/robot_state
cp -r config glennifer/motor_dispatch/src/python

#now actually start the things
cd glennifer/HCI
mvn compile
nohup mvn exec:java -Dexec.mainClass="com.cwrubotix.glennifer.hci.ModuleMain" &> /home/glennifer/logs/$datestring/ModuleMainOutput.log &
cd ..

cd robot_state
mvn compile
nohup mvn exec:java -Dexec.mainClass="com.cwrubotix.glennifer.robot_state.StateModule" &> /home/glennifer/logs/$datestring/StateModuleOutput.log &
cd ..

cd motor_dispatch/src/python
nohup python3 locomotion.py &> /home/glennifer/logs/$datestring/LocomotionPyOutput.log &
cd ../../..

cd client-cameras
nohup python3 client-cam-send.py &> /home/glennifer/logs/$datestring/CameraSendPyOutput.log &
cd ..

