/**
 * This is the class for meassuring time and laps
 */
#ifndef ServoTimer_h
#define ServoTimer_h

#include "Arduino.h"

class ServoTimer {
  public:
    /**
    * Constructor
    */
    ServoTimer(int timerPin);

    /**
    * does the timing measuring
    */
    void doTiming(boolean powerOn);

    /**
    * prints the data to serial
    */
    void writeToSerial();
    
    /**
    * is called when an interrupt happens on the pin for the timer
    */
    void handleInterrupt();
   
  private:
    int _timePin = 0;
    unsigned long _lastReadedTime = 0;
    unsigned long _lapTime = 0;
    unsigned long _btnDebounce=0;
    // marks if the car hits the timer
    boolean _lapCount = false;
    
    String _serialData;
};
#endif


