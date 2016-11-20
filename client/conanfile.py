from conans import ConanFile, CMake

class GlenniferClientConan(ConanFile):
   settings = "os", "compiler", "build_type", "arch"
   requires = "librabbitmq/0.8.1@filonovpv/stable", "amqpcpp/2.6.2@theirix/stable"#, "Protobuf/2.6.1@memsharded/testing"
   generators = "qmake"
   build_policy = "missing"
