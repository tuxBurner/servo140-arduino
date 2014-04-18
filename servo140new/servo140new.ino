#include <ServoCar.h>
#include <ServoTimer.h>

// car1
ServoCar car1(true,2,4,12,3);
// car 2
ServoCar car2(false,7,8,13,11);

// timer
ServoTimer timer1(A5);
ServoTimer timer2(A4);

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
  pinMode(datapin, OUTPUT);
  pinMode(clockpin, OUTPUT);  
  pinMode(latchpin, OUTPUT);

  // turn on power led
  shiftWrite(powerLedPos, HIGH);
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
 * Gets the value from the serialInput
 */
String getValueFromSerialInput(String data,  int index)
{
  char separator = ',';
  int found = 0;
  int strIndex[] = {
    0, -1                    };
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

  unsigned long currMillis = millis();  

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












