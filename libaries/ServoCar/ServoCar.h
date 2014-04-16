/**
 * This is the class for controlling a servo 140 carrera car
 */
#ifndef ServoCar_h
#define ServoCar_h

#include "Arduino.h"

class ServoCar {
  public:
   ServoCar(boolean ghostCar,boolean steerRight, int steerPin, int throttlePin, int motorPin1, int motorPin2);

   /**
   * Read the current data from the remote reciever
   */
   void readData();

   /**
   * Does the motor controll and fuel handling
   */
   void controllMotor(boolean powerOn); 

   /**
   * the current car data
   */
   void dataToSerial();
   /**
   * prints the settings to the serial
   */
   void settingsToSerial();  
   
   /**
   * sets if to steer right or not mainly used for ghostcar
   */
   void setSteerRight(boolean steerRight);
   /**
   * sets the thrust mainly used for ghostcar
   */
   void setThrust(int thrust);
   /**
   * sets wether this car is a ghost car or not
   */
   void setIsGhostCar(boolean ghostCar);

   /**
   * marks if the car cares of fuel or not
   */
   void setCareOfFuel(boolean careOfFuel);

   /**
   * sets how many fuel is in the tank
   */
   void setFullFuel(unsigned long fuelFull);

   /**
   * when is reserve of the fuel reached
   */
   void setOnReserve(unsigned long onReserve);
  
   /**
   * how long does it take to refill 
   */
   void setRefillTime(long refillTime);

   /**
   * Returns if the break was hit or not
   */
   boolean breakHit();
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
    boolean _careOfFuel = false;
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


