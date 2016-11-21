from conans import ConanFile, CMake

class GlenniferClientConan(ConanFile):
   settings = "os", "compiler", "build_type", "arch"
   requires = "librabbitmq/0.8.1@filonovpv/stable", "amqp/1.0-snapshot@ichaelm/testing"#, "Protobuf/2.6.1@memsharded/testing"
   generators = "qmake"
   build_policy = "missing"
