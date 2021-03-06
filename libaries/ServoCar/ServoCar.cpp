/*
* ServoCar reads data from a rc remote or runs the car in gostmode
*/
#include "Arduino.h"
#include "ServoCar.h"

ServoCar::ServoCar(boolean steerRight,int steerPin, int throttlePin, int motorPin1, int motorPin2) {
  // store the vars from the constructor to the private vars	
	_steerPin = steerPin;
  _throttlePin = throttlePin;
  _motorPin1 = motorPin1;
  _motorPin2 = motorPin2; 
  _steerRight = steerRight;

	// normal mode has input
	if(_ghostCar == false) {
    pinMode(_steerPin, INPUT); 
    pinMode(_throttlePin,INPUT);
    // later we need this
    //pinMode(thrust1LedPin,OUTPUT);
    // read the 0 based values for the steering and the throttle
    _initSteerValue = pulseIn (_steerPin, HIGH); 
    _initThrottleVale = pulseIn (_throttlePin, HIGH);
	} 

  pinMode(_motorPin1,OUTPUT);
  pinMode(_motorPin2,OUTPUT);
}

/**
* Reads the data and evaluates it.
*/
void ServoCar::readData() {

  // skip all the reading if it is a ghostcar
  if(_ghostCar == true) {
    return;
  }

  // read the steering	
  unsigned long steerValue = pulseIn (_steerPin, HIGH, 20000);
  int steer = (steerValue - _initSteerValue);
  steer = constrain (steer, -600, 600); 
  // check if to steer left or right
  if(steer > rSteeringThreashold) {
    _steerRight = true;
  }
  if(steer < lSteeringThreashold) {
    _steerRight = false;
  }

  // read the throttle
  unsigned long throttleValue = pulseIn(_throttlePin, HIGH, 20000); 
  _thrust = throttleValue - _initThrottleVale;
  
  // if break is hit we set this to break
  if(_thrust < -300 && _break == false) {
    unsigned long current = millis();
    if(_breakHitStart == 0) {
      _breakHitStart = current;
    }
    if((current - _breakHitStart) >= 125) {
  	  _break = true;
    }
  } else {
    _breakHitStart = 0;
  	_break = false;
  }
  
  // make sure that the throttle is not flickering
  if(_thrust > thrustThreashold) {
    _thrust = constrain(_thrust, 0, 700); 
    _thrust = map(_thrust, 0, 700, 0, 255);
    _thrust = constrain(_thrust,0,255);
  } else {
    _thrust = 0;
  }
}

void ServoCar::controllMotor(boolean powerOn) {

  // take care of the fuel handling
  if(powerOn == true && _ghostCar == false && _careOfFuel == true) {
    unsigned long current = millis();

    // evry fourth seconds remove the current thrust from the fuel
    if(_lastFuelRead == 0 || current - _lastFuelRead >= 250) {
      _lastFuelRead = current;
      _fuel = _fuel -_thrust;
    }
  }

  // car is on reserve well half thrust
  if(_fuel <= _carOnReserve) {    
    _thrust = constrain(_thrust,0,125);
  }

  // refill the fuel
  if(_fuel < 0) {
    _thrust = 0;
    unsigned long  current = millis();
    if(_refillTimer == 0) {
      _lastFuelTimRead = current + _refillTime;
    }

    _refillTimer = _lastFuelTimRead - current;  
    if(_refillTimer <=  0) {
      _fuel = _fuelFull;
      _refillTimer = 0;
    }
  }

  if(powerOn == true && _fuel >= 0) {
    if(_steerRight == true) {
      digitalWrite(_motorPin1, LOW);
      analogWrite(_motorPin2,_thrust);
    } else {
      digitalWrite(_motorPin2, LOW);
      analogWrite(_motorPin1,_thrust);
    }
  } else {
    analogWrite(_motorPin2,0);
    analogWrite(_motorPin1,0);
  }
}

void ServoCar::dataToSerial() {
  Serial.print(_thrust);
  Serial.print(",");
  Serial.print(_steerRight);
  Serial.print(",");
  Serial.print(_fuel);
  Serial.print(",");
  Serial.print(_refillTimer);
}


void ServoCar::settingsToSerial() {
  Serial.print(_ghostCar);
  Serial.print(",");
  Serial.print(_thrust);
  Serial.print(",");
  Serial.print(_steerRight);
  Serial.print(",");
  Serial.print(_careOfFuel);
  Serial.print(",");
  Serial.print(_fuelFull);
  Serial.print(",");
  Serial.print(_carOnReserve);
  Serial.print(",");
  Serial.print(_refillTime);
}


boolean ServoCar::breakHit() {
  return _break;
}

void ServoCar::setThrust(int thrust) {
	_thrust = thrust;
  _thrust = constrain(_thrust,0,255);
}


void ServoCar::setSteerRight(boolean steerRight) {
	_steerRight = steerRight;
}

void ServoCar::setIsGhostCar(boolean ghostCar) {
	_ghostCar = ghostCar;
}

void ServoCar::setCareOfFuel(boolean careOfFuel) {
  _careOfFuel = careOfFuel;
}

void ServoCar::setFullFuel(unsigned long fuelFull) {
  _fuelFull = fuelFull;
  _fuel = fuelFull;
}

void ServoCar::setOnReserve(unsigned long onReserve) {
  _carOnReserve = onReserve;
}
  
   
void ServoCar::setRefillTime(long refillTime) {
  _refillTime = refillTime;
}
