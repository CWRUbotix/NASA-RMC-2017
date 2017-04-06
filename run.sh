
#build protobuf
./build-pb.sh

#make sure stuff is compiled
mvn compile

#now actually start the things
cd glennifer/HCI/src/main/java/com/cwrubotix/glennifer/hci
mvn compile

mvn exec:java -Dexec.mainClass="com.cwrubotix.glennifer.hci.ModuleMain" &
cd ../../../../../../../..
cd robot_state/src/main/java/com/gwrubotix/glennifer/robot_state

mvn compile
mvn exec:java -Dexec.mainClass="com.cwrubotix.glennifer.robot_state.StateModule" &
cd ../../../../../../../..

cd motor_dispatch/src/python
python3 locomotion.py &

