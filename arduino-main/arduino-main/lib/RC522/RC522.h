#include <SoftwareSerial.h>
#include <Arduino.h>
#include <wifi.h>
#include <rdm6300.h>
#include <WiFiS3.h>
#include <ArduinoJson.h>
#include <led.h>
#include <SPI.h>
#include <MFRC522.h>

void rcSetup();
String nfcRead();