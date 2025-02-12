#include <Arduino.h>
#include <SoftwareSerial.h>

/** Configures LED pins and sets their initial states. */
void ledSetup()
{
    pinMode(8, OUTPUT);
    digitalWrite(8, LOW);
    pinMode(10, OUTPUT);
    digitalWrite(10, HIGH);
}

/** Activates "open" state: green LED lights up for 5 seconds. */
void ledOpen()
{
    digitalWrite(10, LOW); // Turn off "locked" LED
    digitalWrite(8, HIGH); // Turn on "open" LED
    delay(5000);
    digitalWrite(8, LOW);
    digitalWrite(10, HIGH); // Restore default state
}

/** Flashes the red LED five times. */
void ledWrong()
{
    for (int i = 0; i < 5; i++)
    {
        digitalWrite(10, LOW);
        delay(500);
        digitalWrite(10, HIGH);
        delay(500);
    }
}

/** Alternates LEDs five times for registration mode. */
void ledRegister()
{
    for (int i = 0; i < 5; i++)
    {
        digitalWrite(10, HIGH);
        digitalWrite(8, LOW);
        delay(400);
        digitalWrite(10, LOW);
        digitalWrite(8, HIGH);
        delay(400);
    }
    digitalWrite(10, HIGH); // Restore default state
    digitalWrite(8, LOW);
}
