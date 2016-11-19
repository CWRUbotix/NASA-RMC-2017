# This PowerShell script sets up everything you need to build and run the client on Windows.

# This script has the following prerequisites:
# * You are running a64-bit windows operating system
# * You have installed Visual Studio 2015
# * You have installed CMake, and it is in your system PATH
# * You have installed Qt
# * You have installed git for windows, and it is in your system PATH
# * You have installed Conan for windows

# Set environment variables for Visual Studio 2015 Command Prompt
# Copied from http://stackoverflow.com/questions/2124753/how-i-can-use-powershell-with-the-visual-studio-command-prompt

pushd 'C:\Program Files (x86)\Microsoft Visual Studio 14.0\VC'
cmd /c "vcvarsall.bat amd64 & set" |
foreach {
  if ($_ -match "=") {
    $v = $_.split("="); set-item -force -path "ENV:\$($v[0])"  -value "$($v[1])"
  }
}
popd
write-host "`nVisual Studio 2015 Command Prompt variables set." -ForegroundColor Yellow

# Download and build protobuf 3.1.0
$FileExists = Test-Path .\protobuf-3.1.0\ 
If ($FileExists -eq $False) {
	wget https://github.com/google/protobuf/releases/download/v3.1.0/protobuf-cpp-3.1.0.zip -OutFile protobuf-cpp-3.1.0.zip
	Expand-Archive protobuf-cpp-3.1.0.zip -dest . -Force
	rm protobuf-cpp-3.1.0.zip
}


cd .\protobuf-3.1.0\cmake\

New-Item -Force -ItemType directory -Path build
cd .\build\
New-Item -Force -ItemType directory -Path release
cd .\release\

cmake -G "NMake Makefiles" -DCMAKE_BUILD_TYPE=Release -Dprotobuf_BUILD_TESTS=OFF -Dprotobuf_BUILD_SHARED_LIBS=ON ../..

nmake

cd ..
cd ..
cd ..\..

# Use conan to install other deps (rabbitmq-c)

cd client
conan install
cd ..


