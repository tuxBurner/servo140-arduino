#include <ServoCar.h>

// car1
ServoCar car1(false,true,2,4,12,3);
// car 2
ServoCar car2(true,false,-1,-1,13,11);

// start light pins
const int datapin = 12; 
const int clockpin = 6;
const int latchpin = 10;
// data of the start light
byte startLightData = 0;
const int startLightDelayTime = 750;
const int numOfLights=3;
/**
 * the current start light if -1 the start light is turned of
 */
int currStartLight = -1;
// flag if the power is on or not
boolean powerOn = true;
// const which pin the power led in on the ic
const int powerLedPos = 5;
/**
 * the last readed millis
 */
int lastMillis = 0;

void setup()
{
  /**
   * Prepare serial 
   **/
  Serial.begin(9600);

  /**
   *  setup start light pins
   */
  pinMode(datapin, OUTPUT);
  pinMode(clockpin, OUTPUT);  
  pinMode(latchpin, OUTPUT);

  // turn on power led
  shiftWrite(powerLedPos, HIGH);
  
  car2.setThrust(125);
}

void loop()
{
  startLightControl();

  car1.readData();
  car2.readData();

  boolean breakHit = car1.breakHit() || car2.breakHit();
  if(breakHit) {
    // power of the lane
    if(powerOn == true) {
      powerOn = false;
      shiftWrite(powerLedPos, LOW);
      delay(500);
      return;
    } 
    // start the light sequence
    if(powerOn == false) {
      // start the lightSequence
      currStartLight = 0;
    }
  }
  
  Serial.print(currStartLight);
  Serial.print(",");
  Serial.print(powerOn);
  Serial.print(",");
  car1.dataToSerial();
  Serial.print(",");
  car2.dataToSerial();
  Serial.println("");

  car1.controllMotor(powerOn);
  car2.controllMotor(powerOn);
}

/**
 * Controls the start light sequence and if sequence is done turns the power on
 */
void startLightControl() {

  // do nothing when marked as off
  if(currStartLight == -1) {
    return;
  }

  // okay lets start the clock timer
  if(currStartLight == 0 && lastMillis == 0) {
    // set the last milliseconds
    lastMillis = millis();
  }

  int currMillis = millis();  

  // we reached the maximum delay time next light please
  if(currMillis - lastMillis > startLightDelayTime) {
    if(currStartLight <= numOfLights) {
      // turn on the light
      shiftWrite(currStartLight, HIGH); 
      // reset the clock timer
      lastMillis = millis();
      // increment to next light
      currStartLight++;
    } 
    else {
      // turn of the lights
      for(int index = numOfLights; index >= 0; index--) {
        shiftWrite(index, LOW);
      }
      // reset last millis
      lastMillis = 0;
      // no start light
      currStartLight = -1;
      // turn the power on
      powerOn = true;
      // turn on the power led
      shiftWrite(powerLedPos, HIGH);
    }  
  }  
}

/**
 * write the data to the start light 
 */
void shiftWrite(int desiredPin, boolean desiredState) {
  bitWrite(startLightData,desiredPin,desiredState);
  shiftOut(datapin, clockpin, MSBFIRST, startLightData);
  digitalWrite(latchpin, HIGH);
  digitalWrite(latchpin, LOW);
}


