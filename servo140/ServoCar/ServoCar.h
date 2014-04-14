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
   void settingsToSerial();  
  private:
    // threashholds
    const int rSteeringThreashold = 250;
    const int lSteeringThreashold = rSteeringThreashold * -1;
    const int thrustThreashold = 45;    
    unsigned long _initSteerValue;
    unsigned long _initThrottleVale;
    // pins
    int _steerPin;
    int _throttlePin;
    int _motorPin1;
    int _motorPin2;
    int _thrust;    
    // is it a ghost car 
    boolean _ghostCar;
    // steer right or left
    boolean _steerRight;
    // hit the break
    boolean _break;    
    // fuel vars
    boolean _careOfFuel = true;
    unsigned long _fuelFull = 10000;
    long _fuel = _fuelFull;
    unsigned  long _carOnReserve = 1000;
    int _lastFuelRead = 0;
    // how long does it take to refill ?
    int _lastFuelTimRead = 0;
    long _refillTime = 10000;
    long _refillTimer = 0;
};
#endif


