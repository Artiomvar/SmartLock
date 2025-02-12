# Smart Lock Project Team 3

## Overview
This project implements a versatile smart lock system using RFID-based authentication, integrated with an Arduino and connected to a backend server for user validation. While NFC functionality is being explored and may not work, fingerprint authentication has been discarded due to issues with the sensor or its software/library support. The system supports:

- Unlocking using RFID authentication.
- Registering new RFID tags directly through the lock system.
- Communicating with a backend server for user validation and access management.
- Remote access control via the backend server.

---

## Features
- **RFID-Based Authentication**:
  - Uses an RDM6300 RFID reader to scan tags for access control.
- **Wi-Fi Connectivity**:
  - Connects to a Wi-Fi network to communicate with a Spring Boot backend server.
- **Backend Integration**:
  - Sends RFID tag data to the backend for validation.
  - Supports remote access: backend users can send unlock commands to the lock system.
- **Dynamic Lock Control**:
  - Unlocks the lock only when validation is successful, either through local RFID scanning or remote backend commands.
- **Register New Tags**:
  - Allows registering new RFID tags by sending a registration request. The system will prompt the user to scan a new tag, which is then sent to the backend for storage.

---

## Components Used

### Hardware
- **Arduino Board**: UNO R4 WiFi.
- **RDM6300 RFID Reader**: Scans RFID cards for authentication.
- **Wi-Fi Module**: Built-in Arduino WiFiS3.
- **Lock Control**: Two LEDs to represent lock states:
  - Green LED: Indicates the lock is unlocked.
  - Red LED: Indicates the lock is locked.

### Software Libraries
- `ArduinoJson`
- `WiFiS3`
- `ArduinoHttpClient`

---

## How It Works

1. **Unlocking via RFID**:
   - A user scans an RFID tag using the RDM6300.
   - The scanned tag data is sent to the backend server.
   - If the backend validates the tag, the lock is unlocked (Green LED turns on).

2. **Remote Access**:
   - The backend server can send unlock commands to the lock system.
   - When the lock receives a valid unlock request, it triggers the unlocking mechanism (Green LED).

3. **Registering a New Tag**:
   - The user sends a registration request to the lock.
   - The system prompts the user to scan a new tag (Red and Green LEDs blink).
   - Once scanned, the new tag data is sent to the backend for storage.
   - The system confirms successful registration via a response from the backend.

---

## Current Limitations
- **NFC Functionality**: Currently under development and may not work reliably, and we might have to dicard this one if we cannot figure it out.
- **Fingerprint Authentication**: Discarded due to sensor issues or compatibility problems.

---

## Team Members
- Kasper Vandevenne
- Artiom Varvarenko
- Bayram Yilmaz
- Mitko Stoynov
- Maimuna Bah
- Alexandra Gavril

Kasper Vandevenne is primarily responsible for most of the Arduino-related aspects of the project.

NFC has unfortunatly also been discarded due to complications with the sensor.

