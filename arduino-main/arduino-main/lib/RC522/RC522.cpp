#include <SPI.h>
#include <MFRC522.h> //this sensor will use the premade library MFRC522.h by miguelbalboa

#define RST_PIN 9 // Reset pin
#define SS_PIN 7 // Slave Select (CS) pin

MFRC522 nfc(SS_PIN, RST_PIN); // Create MFRC522 instance

void rcSetup(){
  SPI.begin(); // Initialize SPI bus
  nfc.PCD_Init(); // Initialize RC522 module
  digitalWrite(10, HIGH);
  digitalWrite(7, LOW);
  nfc.PCD_DumpVersionToSerial(); // Show details of PCD - RC522 Card Reader

  if (nfc.PCD_PerformSelfTest())
  {
    Serial.println(F("Self test passed. RC522 is ready."));
  }
  else
  {
    Serial.println(F("Self test failed. Check connections."));
  }
  Serial.println("NFC reader ready for use!");
}

String nfcRead()
{
  String cardData = ""; // Initialize the variable to store card data

  // Check if there is a new card
  if (nfc.PICC_IsNewCardPresent())
  {
    Serial.println("New card detected!"); // Debugging

    // Attempt to read the card's serial number
    if (nfc.PICC_ReadCardSerial())
    {
      Serial.println("Card serial read!"); // Debugging

      // Read the UID and store it as a hex string
      for (int8_t i = 0; i < nfc.uid.size; i++)
      { // Use int8_t to avoid issues with signed/unsigned char
        if (nfc.uid.uidByte[i] < 0x10)
        {
          cardData += "0"; // Add leading zero for single-digit hex values
        }
        cardData += String(nfc.uid.uidByte[i], HEX); // Append the byte as hex
      }

      // Print out the card data for debugging
      Serial.println("Card Data: " + cardData);

      // Halt PICC communication and stop encryption
      nfc.PICC_HaltA();
      nfc.PCD_StopCrypto1();

      delay(10); // Debounce to avoid reading too fast

      return cardData; // Return the card UID as a string
    }
    else
    {
      // Failed to read card serial
      Serial.println("Failed to read card serial!"); // Debugging
    }
  }
  else
  {
    // Return empty string if no card is detected or read
    return "";
  }

  
}
