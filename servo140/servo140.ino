#include <Servo.h>

const float throttleScaling = .15;
const float steerScaling = .15;
const int steeringThreashold = 10;
const int thrustThreashold = 20;

// start light pins
const int datapin = 12; 
const int clockpin = 6;
const int latchpin = 10;
// data of the start light
byte startLightData = 0;
boolean lightOkay = false;

// flag if the power is on or not
boolean powerOn = false;

/**
* Car 1 vars
*/
// read pins
const int steer1Pin = 2; // which pin to read for steer car1
const int throttle1Pin = 4; // which pin to read to controll throttle car1

// motorctrl pins
const int motor1DirPin = 12;
const int motor1PwmPin = 3;

// display led pins
const int steer1LedPin = 7;
const int thrust1LedPin = 9;

unsigned long steer1Value; // the current value of throttle 1
unsigned long throttle1Value; // the current value of throttle 1
unsigned long initialSteer1; // the value of the initial steering
unsigned long initialThrottle1; // the value of the initial throttle 1
boolean right1 = true; // if car 1 is steering right
int steer1 = 0; // the calculated value of car1 steering
int thrust1 = 0; // the calculated value of car1 throttle


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

  /**
  * Setup car1 
  */
  // set pin for steer1 and throttle1 to digital read for car1
  pinMode(steer1Pin, INPUT); 
  pinMode(throttle1Pin,INPUT);
  pinMode(steer1LedPin, OUTPUT);
  pinMode(thrust1LedPin,OUTPUT);
  // read the default values for car1
  initialSteer1 = pulseIn (steer1Pin, HIGH); // read initialsteer value
  initialThrottle1 = pulseIn (throttle1Pin, HIGH);
}



void loop()
{
  if(lightOkay == false) {
    startLightSeq();
  }  
  
  /**
  * Read car1 steering value
  */
  steer1Value = pulseIn (steer1Pin, HIGH, 20000);
  steer1 = (steer1Value - initialSteer1);
  steer1 = steer1 * steerScaling;
  steer1 = constrain (steer1, -100, 100); 
  // check if to steer left or right
  if(steer1 > steeringThreashold) {
    right1 = true;
  }
  if(steer1 < steeringThreashold * -1) {
    right1 = false;
  }
  /*Serial.print ("Initial Steer: ");
  Serial.print (initialSteer1);
  Serial.print (" Channel 1: ");
  Serial.print (steer1Value);
  Serial.print(" Steer: ");
  Serial.print (steer1);
  Serial.print (" Right: ");
  Serial.print (right1);
  Serial.println("");*/

  /**
  * Read car1 throttle value
  */
  throttle1Value = pulseIn(throttle1Pin, HIGH, 20000); //read RC channel 2
  thrust1 = throttle1Value - initialThrottle1;
  
  // reset if break is hit for debug
  if(thrust1 < -100) {
    lightOkay = false;
    powerOn = false;  
  }
  
  // make sure that the throttle is not flickering
  if(thrust1 > thrustThreashold) {
    thrust1 = thrust1 * throttleScaling; // convert to 0-100 range-c
    thrust1 = constrain(thrust1, 0, 100); //just in case
  } else {
    thrust1 = 0;
  }
 /* Serial.print("Initial Throttle: ");
  Serial.print (initialThrottle1);
  Serial.print(" Channel 2: ");
  Serial.print (throttle1Value);
  Serial.print(" Thrust: ");
  Serial.print (thrust1);
  Serial.println("");*/
  Serial.print("Thrust: ");
  Serial.print(thrust1);
  Serial.print(" Steering: ");
  Serial.print(right1);
  Serial.println("");
  
  if(right1 == true) {
    digitalWrite(steer1LedPin, HIGH);
    digitalWrite(motor1DirPin, HIGH);
  } else {
    digitalWrite(steer1LedPin, LOW);
    digitalWrite(motor1DirPin, LOW);
  }
  
  
  
  // spin the motor
  int val = map(thrust1, 0, 100, 0, 255);
  analogWrite(thrust1LedPin, val);
  // only start motor if power is on
  if(powerOn == true) {
    analogWrite(motor1PwmPin,val);
  }  
}

void startLightSeq() {
  int index;
  const int delayTime = 500; // Time (milliseconds) to pause between LEDs
                       // Make this smaller for faster switching

  // Turn all the LEDs on:
 
  // This for() loop will step index from 0 to 7
  // (putting "++" after a variable means add one to it)
  // and will then use digitalWrite() to turn that LED on.
  
  for(index = 0; index <= 5; index++)
  {
    shiftWrite(index, HIGH);
    delay(delayTime);                
  }

  // Turn all the LEDs off:

  // This for() loop will step index from 7 to 0
  // (putting "--" after a variable means subtract one from it)
  // and will then use digitalWrite() to turn that LED off.
 
  for(index = 5; index >= 0; index--)
  {
    shiftWrite(index, LOW);
    //delay(delayTime);
  }
  lightOkay = true;
  
  // turn the power on
  powerOn = true;
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






