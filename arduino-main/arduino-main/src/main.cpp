#include <SoftwareSerial.h>
#include <Arduino.h>
#include <wifi.h>
#include <rdm6300.h>
#include <WiFiS3.h>
#include <ArduinoJson.h>
#include <led.h>
#include <RC522.h>
#include <SPI.h>
#include <MFRC522.h>

void setup()
{
    Serial.begin(9600);
    wifiSetup();
    rdmSetup();
    ledSetup();
    //rcSetup();
} 

void loop()
{
    
    // Check if there is a tag being scanned
    String scannedTag = rdmRead(); // Try reading a tag
    if (scannedTag.length() > 5)
    {
        // A valid tag is detected, so handle it
        Serial.println("Scanned Tag: " + scannedTag); // Print the scanned tag

        sendData(scannedTag, "RFID");
    }

    handlePostRequest(); // This is called regardless of tag status


    // Wait for the tag reader to clear before continuing
    while (rdmRead().length() > 5)
    {
        delay(50); // Delay while waiting for the reader to clear
    }
    

    delay(400); // Delay for stability
}
