#include <SoftwareSerial.h>
#include <Arduino.h>

// Create a software serial object for RFID communication using pins 6 (TX) and 2 (RX)
SoftwareSerial RFID(6, 2);


String text;


/**
 * Sets up the RFID reader by initializing the SoftwareSerial object.
 * This function also sends a message to the serial monitor indicating readiness.
 */
void rdmSetup()
{
  RFID.begin(9600); // Initialize the RFID reader at 9600 baud rate
  Serial.println("RFID reader ready for use!");
}




/**
 * Reads data from the RFID reader.
 * - Waits for an RFID tag to be scanned and processes its data.
 * - Recognizes the start of the tag with the character '\x02' and the end with '\x03'.
 * - Returns the detected RFID tag's data as a string.
 *
 * @return String: The detected RFID tag's data. Returns an empty string if no valid tag is read.
 */
String rdmRead()
{
  String text = "";        // Holds the tag data as it's read
  bool tagStarted = false; // Tracks whether tag reading has started

  // Continue reading from the RFID reader while data is available
  while (RFID.available() > 0)
  {
    delay(5);             // Small delay to allow the buffer to fill
    char c = RFID.read(); // Read one character from the RFID reader

    if (c == '\x02')
    { // Start character for the tag (␂)
      tagStarted = true;
      text = ""; // Reset text to clear previous tag data
    }
    else if (c == '\x03')
    { // Stop character for the tag (␃)
      if (tagStarted)
      {
        tagStarted = false; // End tag reading
        if (text.length() > 0)
        {                                          // Ensure the tag contains valid data
          Serial.println("Tag detected: " + text); // Log the detected tag
          while (RFID.available() > 0)
          {
            RFID.read(); // Clear any remaining characters in the buffer
            delay(5);
          }
          delay(1000); // Add a 1-second delay to debounce
          return text; // Return the valid tag
        }
      }
    }
    else if (tagStarted)
    {
      text += c; // Append the current character to the tag data
    }
  }

  return ""; // Return an empty string if no valid tag was detected
}

/*
 * The stop character '\x03' signals the end of the tag but is not included in the
 * actual returned or printed data. Only the characters between '\x02' (start)
 * and '\x03' (stop) are processed and displayed as the tag data.
 */
