#include <ServoCar.h>
#include <ServoTimer.h>
#include <FastLED.h>

// car1
ServoCar car1(true,8,7,9,6);
// car 2
ServoCar car2(false,12,13,10,11);

// timers
ServoTimer timer1(2);
ServoTimer timer2(3);

// start light pins
const int ledsPin = 4;
const int numLeds = 8;
CRGB leds[numLeds];
// data of the start light
const int startLightDelayTime = 750;
const int numOfLights=3;
/**
 * the current start light if -1 the start light is turned of
 */
int currStartLight = -1;
// flag if the power is on or not
boolean powerOn = true;

/**
 * the last readed millis
 */
unsigned long lastMillis = 0;

/**
 * string which stores the data of the serial input
 */
String serialInputStr;

void setup()
{
  /**
   * Prepare serial 
   **/
  Serial.begin(9600);

  /**
   *  setup start light pins
   */
  FastLED.addLeds<WS2812B, ledsPin,GRB>(leds, numLeds);
  turnPowerLedsOn(CRGB::Green);

  // attach the interrupt methods for the timer
  attachInterrupt(0, handleTimer1, LOW);
  attachInterrupt(1, handleTimer2, LOW);
}

void loop()
{

  // when data comes in we read until
  while (Serial.available() > 0) {
    serialInputStr = Serial.readStringUntil('\n');
    // return the current settings
    if(serialInputStr.startsWith("G")) {
      printCurrentSettings();
    } 
    else if(serialInputStr.startsWith("H")) {
      powerOn = !powerOn;
    } 
    else if(serialInputStr.startsWith("L")) {
      powerOn = false;
      currStartLight = 0;
    }
    else {
      setCarValues(car1,0);
      setCarValues(car2,7);
      printCurrentSettings();
    }  
  }

  startLightControl();

  timer1.doTiming(powerOn);
  timer2.doTiming(powerOn);

  car1.readData();
  car2.readData();

  boolean breakHit = car1.breakHit() || car2.breakHit();
  if(breakHit) {
    // power of the lane
    if(powerOn == true) {
      powerOn = false;
      for(int index = numOfLights; index >= 0; index--) {
        leds[index] = CRGB::Blue;
        leds[index+4] = CRGB::Blue;
      }
      FastLED.show();
      delay(500);
      return;
    } 
    // start the light sequence
    if(powerOn == false) {
      // start the lightSequence
      currStartLight = 0;
    }
  }

  car1.controllMotor(powerOn);
  car2.controllMotor(powerOn);

  Serial.print(currStartLight);
  Serial.print(",");
  Serial.print(powerOn);
  Serial.print(",");
  car1.dataToSerial();
  Serial.print(",");
  car2.dataToSerial();
  Serial.print(",");
  timer1.writeToSerial();
  Serial.print(",");
  timer2.writeToSerial();
  Serial.println("");

}

/**
 * Returns the currentSettings
 */
void printCurrentSettings() {
  Serial.print("S,");
  car1.settingsToSerial();
  Serial.print(",");
  car2.settingsToSerial();  
  Serial.println("");
}

/**
 * sets the values from the setting at the given car
 */
void setCarValues(ServoCar &car, int fromIndex) {
  int index = fromIndex;
  boolean ghostCar = (getValueFromSerialInput(serialInputStr,index++) == "1");
  int carThrust = getValueFromSerialInput(serialInputStr,index++).toInt();
  boolean steerRight = (getValueFromSerialInput(serialInputStr,index++) == "1");
  boolean careOfFuel = (getValueFromSerialInput(serialInputStr,index++) == "1");
  long fullFuel = getValueFromSerialInput(serialInputStr,index++).toInt();
  long onReserve = getValueFromSerialInput(serialInputStr,index++).toInt();
  long refillTime = getValueFromSerialInput(serialInputStr,index++).toInt();

  car.setIsGhostCar(ghostCar);
  car.setThrust(carThrust);
  car.setSteerRight(steerRight);
  car.setCareOfFuel(careOfFuel);
  car.setFullFuel(fullFuel);
  car.setOnReserve(onReserve);
  car.setRefillTime(refillTime);
}

/**
 * Gets the value from the serialInput by spliiting it by ,
 */
String getValueFromSerialInput(String data,  int index)
{
  char separator = ',';
  int found = 0;
  int strIndex[] = {
    0, -1                      };
  int maxIndex = data.length()-1;

  for(int i=0; i<=maxIndex && found<=index; i++){
    if(data.charAt(i)==separator || i==maxIndex){
      found++;
      strIndex[0] = strIndex[1]+1;
      strIndex[1] = (i == maxIndex) ? i+1 : i;
    }
  }

  return found>index ? data.substring(strIndex[0], strIndex[1]) : "";
}


/**
 * interrupt method for timer 1
 */
void handleTimer1() {
  timer1.handleInterrupt();
}

/**
 * interrupt method for timer 2
 */
void handleTimer2() {
  timer2.handleInterrupt();
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
      turnPowerLedsOn(CRGB::Black);
  }

  unsigned long currMillis = millis();  

  // we reached the maximum delay time next light please
  if(currMillis - lastMillis > startLightDelayTime) {
    if(currStartLight <= numOfLights) {
      // turn on the light
      leds[currStartLight] = CRGB::Red;
      leds[currStartLight+4] = CRGB::Red;
      FastLED.show();
      // reset the clock timer
      lastMillis = millis();
      // increment to next light
      currStartLight++;
    } 
    else {
      turnPowerLedsOn(CRGB::Green);
      // reset last millis
      lastMillis = 0;
      // no start light
      currStartLight = -1;
      // turn the power on
      powerOn = true;
    }  
  }  
}

/**
 * Turns the leds green which marks that the power is on
 */
void turnPowerLedsOn(CRGB color) {
  for(int index = 4; index >= 0; index--) {
    leds[index] = color;
    leds[index+4] = color;
  }
  FastLED.show();
}












