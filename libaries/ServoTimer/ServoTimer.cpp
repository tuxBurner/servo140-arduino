#include "Arduino.h"
#include "ServoTimer.h"

ServoTimer::ServoTimer(int timerPin) {
  _timePin = timerPin;
  pinMode(_timePin, INPUT_PULLUP);
}

void ServoTimer::handleInterrupt() {
  _lapCount = true;
}

void ServoTimer::doTiming(boolean powerOn) {
  unsigned long current = millis();  
  // no time action when power ist off
  if(powerOn == false) {
    _lastReadedTime = current;
    return;
  }

  unsigned long diff = current - _lastReadedTime;
  int sensorVal = digitalRead(_timePin);
  _lapTime+= diff;

  _serialData=""; //_lapTime;
  _serialData+=_lapTime;
  _serialData+=",";
  

  if(_lapCount == true) {
    if(current - _btnDebounce > 500) {
      _btnDebounce = current;
      _lapTime = 0;
    }
  }
  _serialData+=_lapCount;
  
  _lapCount = false;
  _lastReadedTime = current;
}

void ServoTimer::writeToSerial() {
  Serial.print(_serialData);  
}