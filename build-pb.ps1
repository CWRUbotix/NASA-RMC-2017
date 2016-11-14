# This script builds all the protobuf classes for java, cpp, and python on windows

New-Item -Force -ItemType directory -Path pb

.\protobuf-3.1.0\cmake\build\release\protoc.exe --cpp_out=pb/. --java_out=pb/. --python_out=pb/. --python_out=glennifer/. messages.proto