# This script builds all the protobuf classes for java, cpp, and python on windows

New-Item -Force -ItemType directory -Path pb

.\client\bin\protoc.exe --cpp_out=pb/. --java_out=pb/. --python_out=pb/. --python_out=glennifer/. --cpp_out=client/. messages.proto