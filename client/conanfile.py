from conans import ConanFile, CMake

class GlenniferClientConan(ConanFile):
   settings = "os", "compiler", "build_type", "arch"
   requires = "librabbitmq/0.8.1@filonovpv/stable"#, "Protobuf/2.6.1@memsharded/testing"
   generators = "qmake"