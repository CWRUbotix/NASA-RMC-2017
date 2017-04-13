
mkdir -p glennifer_logs/$(date +%Y_%m_%d_%H_%M_%S)

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
mvn exec:java -Dexec.mainClass="com.cwrubotix.glennifer.hci.ModuleMain" &> /home/glennifer_logs/ModuleMainOutput.log &
cd ..

cd robot_state
mvn compile
mvn exec:java -Dexec.mainClass="com.cwrubotix.glennifer.robot_state.StateModule" &> /home/glennifer_logs/StateModuleOutput.log &
cd ..

cd motor_dispatch/src/python
python3 locomotion.py &> /home/glennifer_logs/LocomotionPyOutput.log &
cd ../../..

cd client-cameras
python3 client-cam-send.py &
cd ..

