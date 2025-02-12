#include <WiFiS3.h>
#include <Arduino.h>
#include <HTTPClient.h>

void printMacAddress(byte mac[]);
String macAddressToString(byte mac[]);
void printCurrentNet();
void printWifiData();
void wifiSetup();
void sendData(String tag, String method);
void handleRegisterRequest(WiFiClient client, String request);
void handlePostRequest();