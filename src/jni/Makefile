
CXX ?= g++
JAVA_HOME ?= /usr/lib/jvm/default-java

all: libdarnwdldynamite.so

org_debian_paulliu_darnwdl_jni_DynamiteJNI.h: ../org/debian/paulliu/darnwdl/jni/DynamiteJNI.java
	javac -h . $<
	rm -f ../org/debian/paulliu/darnwdl/jni/DynamiteJNI.class

org_debian_paulliu_darnwdl_jni_DynamiteJNI.o: org_debian_paulliu_darnwdl_jni_DynamiteJNI.cc org_debian_paulliu_darnwdl_jni_DynamiteJNI.h
	$(CXX) $(CXXFLAGS) -c -Wall -fPIC -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -o $@ $<

libdarnwdldynamite.so: org_debian_paulliu_darnwdl_jni_DynamiteJNI.o
	$(CXX) $(CXXFLAGS) -shared -fPIC -o $@ $< -ldynamite

clean:
	rm -f org_debian_paulliu_darnwdl_jni_DynamiteJNI.h
	rm -f org_debian_paulliu_darnwdl_jni_DynamiteJNI.o
	rm -f libdarnwdldynamite.so
