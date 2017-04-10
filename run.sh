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
mvn exec:java -Dexec.mainClass="com.cwrubotix.glennifer.hci.ModuleMain" &
cd ..

cd robot_state
mvn compile
mvn exec:java -Dexec.mainClass="com.cwrubotix.glennifer.robot_state.StateModule" &
cd ..

cd motor_dispatch/src/python
python3 locomotion.py &
cd ../../..

cd client-cameras
python3 client-cam-send.py &
cd ..

