/**
 * This is the class for controlling a servo 140 carrera car
 */
#ifndef ServoCar_h
#define ServoCar_h

#include "Arduino.h"

class ServoCar {
  public:
   ServoCar(boolean ghostCar,boolean steerRight, int steerPin, int throttlePin, int motorPin1, int motorPin2);
   void readData();
   int getThrust();
   boolean getSteerRight();
   boolean getIsGhostCar();
   boolean breakHit();
   void setSteerRight(boolean steerRight);
   void setThrust(int thrust);
   void setIsGhostCar(boolean ghostCar);
   void controllMotor(boolean powerOn); 
   void dataToSerial();  
  private:
    unsigned long _initSteerValue;
    unsigned long _initThrottleVale;
    int _steerPin;
    int _throttlePin;
    int _motorPin1;
    int _motorPin2;
    int _thrust;
    long _fuel = 100000;
    int _lastFuelRead = 0;
    boolean _ghostCar;
    boolean _steerRight;
    boolean _break;
    const int rSteeringThreashold = 250;
    const int lSteeringThreashold = rSteeringThreashold * -1;
    const int thrustThreashold = 45;
    const int carOutOfFuel = -1000;
};
#endif


