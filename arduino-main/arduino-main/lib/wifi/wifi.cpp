#include <WiFiS3.h>
#include <ArduinoJson.h>
#include <led.h>
#include <rdm6300.h>

// WiFi credentials
char ssid[] = "Kasper iPhone";
char pass[] = "arduino1";
WiFiServer server(80);

const char *serverIP = "172.20.10.8";

/**
 * Converts a MAC address to a human-readable string format and prints it.
 * This is useful for debugging WiFi-related information.
 */
void printMacAddress(byte mac[])
{
  for (int i = 0; i < 6; i++)
  {
    if (i > 0)
    {
      Serial.print(":");
    }
    if (mac[i] < 16)
    {
      Serial.print("0");
    }
    Serial.print(mac[i], HEX);
  }
  Serial.println();
}

/**
 * Converts a MAC address into a `String` format.
 * Returns a string with the MAC address in uppercase for standardization.
 */
String macAddressToString(byte mac[])
{
  String macStr = "";
  for (int i = 0; i < 6; i++)
  {
    if (i > 0)
    {
      macStr += ":";
    }
    if (mac[i] < 16)
    {
      macStr += "0";
    }
    macStr += String(mac[i], HEX);
  }
  macStr.toUpperCase(); // Ensure uniform uppercase format
  return macStr;
}

/**
 * Prints details of the current WiFi network, such as SSID, BSSID (MAC), signal strength (RSSI),
 * and encryption type. Useful for diagnostics during connection setup.
 */
void printCurrentNet()
{
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  byte bssid[6];
  WiFi.BSSID(bssid);
  Serial.print("BSSID: ");
  printMacAddress(bssid);

  long rssi = WiFi.RSSI();
  Serial.print("Signal strength (RSSI): ");
  Serial.println(rssi);

  byte encryption = WiFi.encryptionType();
  Serial.print("Encryption Type: ");
  Serial.println(encryption, HEX);
  Serial.println();
}

IPAddress ip;
byte mac[6];

/**
 * Prints the local IP address and MAC address of the device.
 * These are essential for verifying that the device has connected to the network successfully.
 */
void printWifiData()
{
  ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);

  WiFi.macAddress(mac);
  Serial.print("MAC address: ");
  printMacAddress(mac);
}

/**
 * Attempts to connect the device to the WiFi network using the provided SSID and password.
 * If successful, it starts the server and prints network details. If unsuccessful, prints an error.
 */
void wifiSetup()
{
  delay(2500);
  Serial.print("Attempting to connect to WPA SSID: ");
  Serial.println(ssid);

  WiFi.begin(ssid, pass); // Start WiFi connection
  delay(2000);            // Allow time for connection

  if (WiFi.status() == WL_CONNECTED)
  {
    Serial.println("Connected to the network!");
    server.begin();
    Serial.println("Server started");
    printCurrentNet();
    printWifiData();
  }
  else
  {
    Serial.println("Failed to connect to the network.");
    Serial.println("Please check your WiFi credentials or network status.");
  }
}

/**
 * Sends a JSON payload to a remote server using an HTTP POST request.
 * Parameters:
 * - `tag`: The RFID/NFC/Fingerprint tag or credential to send.
 * - `method`: The method of authentication (e.g., RFID, NFC, FINGER).
 * The server's response is checked to determine success or failure.
 */
void sendData(String tag, String method)
{
  WiFiClient client;

  // Create a JSON document with the required fields
  DynamicJsonDocument doc(1024);
  doc["method"] = method;
  doc["credentials"] = tag;
  doc["mac"] = macAddressToString(mac);
  doc["ipAddress"] = ip;

  // Serialize the JSON document into a string
  String jsonData;
  serializeJson(doc, jsonData);

  Serial.println("Connecting to server...");
  if (client.connect(serverIP, 8080))
  {
    // Prepare and send the HTTP POST request
    client.println("POST /api/unlock HTTP/1.1");
    client.println("Host: " + String(serverIP) + ":" + String(8080));
    client.println("Content-Type: application/json");
    client.print("Content-Length: ");
    client.println(jsonData.length());
    client.println();
    client.println(jsonData);

    Serial.println("Data sent, waiting for response...");
    // Wait for server response
    while (!client.available())
    {
      delay(10);
    }

    // Read the server response
    String responseLine = client.readStringUntil('\r');
    Serial.print("Response: ");
    Serial.println(responseLine);

    // Handle server response
    if (responseLine.indexOf("200") != -1)
    {
      Serial.println("Unlock successful! Triggering door unlock...");
      ledOpen();
    }
    else
    {
      Serial.println("Unlock failed! Response indicates an issue.");
      ledWrong();
    }

    client.stop();
    Serial.println("Disconnected from server.");
  }
  else
  {
    Serial.println("Failed to connect to server.");
  }
}

/**
 * Handles an incoming POST request to the `/register` endpoint.
 * This is called when the user initiates a registration process for a new tag.
 * The LED will start blinking, and the system will wait for a tag to be scanned.
 * After scanning the tag, it sends the tag data to the backend server.
 */
void handleRegisterRequest(WiFiClient client, String request)
{
  if (request.startsWith("POST /register"))
  {
    // Parse the content length
    String jsonData = "";
    int contentLength = 0;

    int startIdx = request.indexOf("Content-Length: ");
    if (startIdx != -1)
    {
      startIdx += 16;
      int endIdx = request.indexOf("\r\n", startIdx);
      contentLength = request.substring(startIdx, endIdx).toInt();
      delay(10);
    }

    // Read the JSON body
    while (client.available() && jsonData.length() < contentLength)
    {
      jsonData += char(client.read());
      delay(10);
    }

    Serial.println("Received POST data for register:");
    Serial.println(jsonData);

    // Parse the JSON data
    StaticJsonDocument<200> doc;
    DeserializationError error = deserializeJson(doc, jsonData);

    if (error)
    {
      Serial.print("Failed to parse JSON: ");
      Serial.println(error.f_str());
      client.println("HTTP/1.1 400 Bad Request");
      client.println("Content-Type: text/plain");
      client.println("Connection: close");
      client.println();
      client.println("Invalid JSON data");
      return;
    }

    // Extract and validate the "method" field
    String method = doc["method"];
    Serial.print("Method received for registration: ");
    Serial.println(method);

    String scannedTag = "";
    if (method == "RFID")
    {
      ledRegister(); // Indicate registration mode with blinking LEDs
      Serial.println("Scan your new card now!");
      while (scannedTag == "")
      {
        scannedTag = rdmRead(); // Wait for tag scan
        delay(100);
      }
    }

    // Send the scanned tag to the backend server
    client.println("HTTP/1.1 200 OK");
    client.println("Content-Type: application/json");
    client.println("Connection: close");
    client.println();
    client.println("{\"status\":\"success\",\"credentials\":\"" + scannedTag + "\"}");
  }

  client.stop();
}

/**
 * Handles incoming POST requests to `/unlock` or `/register` routes.
 * If the request is for `/unlock`, it checks the status of the lock and tries to open it.
 * If the request is for `/register`, it handles the registration of a new tag, triggering LED blinking and waiting for the tag scan.
 */
void handlePostRequest()
{
  WiFiClient client = server.available();
  if (client)
  {
    Serial.println("New client connected");

    // Read the HTTP request
    String request = "";
    while (client.connected())
    {
      if (client.available())
      {
        char c = client.read();
        request += c;
        if (request.endsWith("\r\n\r\n"))
        {
          break;
        }
      }
    }

    Serial.println("Request received:");
    Serial.println(request);

    // Route handling
    if (request.startsWith("POST /unlock"))
    {
      // Handle unlocking (e.g., from the backend)
      String jsonData = "";
      int contentLength = 0;

      int startIdx = request.indexOf("Content-Length: ");
      if (startIdx != -1)
      {
        startIdx += 16;
        int endIdx = request.indexOf("\r\n", startIdx);
        contentLength = request.substring(startIdx, endIdx).toInt();
      }

      while (client.available() && jsonData.length() < contentLength)
      {
        jsonData += char(client.read());
      }

      Serial.println("Received POST data:");
      Serial.println(jsonData);

      StaticJsonDocument<200> doc;
      DeserializationError error = deserializeJson(doc, jsonData);

      if (error)
      {
        Serial.print("Failed to parse JSON: ");
        Serial.println(error.f_str());
        client.println("HTTP/1.1 400 Bad Request");
        client.println("Content-Type: text/plain");
        client.println("Connection: close");
        client.println();
        client.println("Invalid data");
        return;
      }

      // Unlock command logic
      String tag = doc["credentials"];
      String method = doc["method"];
      if (method == "RFID" && tag.length() == 10)
      {
        ledOpen();
        client.println("HTTP/1.1 200 OK");
        client.println("Content-Type: text/plain");
        client.println("Connection: close");
        client.println();
        client.println("Unlock command received");
      }
      else
      {
        ledWrong();
        client.println("HTTP/1.1 400 Bad Request");
        client.println("Content-Type: text/plain");
        client.println("Connection: close");
        client.println();
        client.println("Invalid unlock command");
      }

      client.stop();
    }
    else if (request.startsWith("POST /register"))
    {
      handleRegisterRequest(client, request); // Handle new tag registration
    }
    else
    {
      Serial.println("Invalid route");
      client.println("HTTP/1.1 404 Not Found");
      client.println("Content-Type: text/plain");
      client.println("Connection: close");
      client.println();
      client.println("Invalid route");
      client.stop();
    }
  }
}
