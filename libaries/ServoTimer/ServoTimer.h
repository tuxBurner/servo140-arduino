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
    // the pin where the timer switch is attached to
    int _timePin = 0;

    // the time off the last lap
    unsigned long _lastReadedTime = 0;

    // the time of the lap
    unsigned long _lapTime = 0;

    // the time when the debounce begined
    unsigned long _btnDebounce=0;

    // marks if the car hits the timer
    boolean _lapCount = false;

    // marks if the lap was debounced or not
    boolean _lapCountDebounced = true;

    String _serialData;
};
#endif
