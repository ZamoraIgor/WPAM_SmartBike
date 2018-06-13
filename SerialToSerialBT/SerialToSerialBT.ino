//This example code is in the Public Domain (or CC0 licensed, at your option.)
//By Evandro Copercini - 2018
//
//This example creates a bridge between Serial and Classical Bluetooth (SPP)
//and also demonstrate that SerialBT have the same functionalities of a normal Serial

#include "BluetoothSerial.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

BluetoothSerial SerialBT;

void set_range(){}


 char buff [4];
 char temp [1];
boolean loop1;
char *range1 ="5682.56";
char *battery1 ="12.5585";
char *speed1 ="30.12";
char *power1 ="230.28";
char *deadtime1 ="100.00";
char *sinkcurrent1 ="1000.00";
char *sourcecurrent1 ="2000.00";
char *distance1 ="256.50";

void setup() {
  Serial.begin(9600);
  SerialBT.begin("ESP32test"); //Bluetooth device name
  Serial.println("The device started, now you can pair it with bluetooth!");
  loop1=true;

}
void loop() {
    while(loop1)
    {
    SerialBT.readBytes(temp, 1);
    if (strcmp (temp,"1") == 0)
    {
      loop1=false;
      Serial.write("connected \n");
    }
    }
  if (SerialBT.available()) {
    SerialBT.readBytes(buff, 4);
         if (strcmp (buff,"0001") == 0)SerialBT.println(range1);        //range
    else if (strcmp (buff,"0002") == 0)SerialBT.println(battery1);   //battery
    else if (strcmp (buff,"0003") == 0)SerialBT.println(speed1);     //current_speed
    else if (strcmp (buff,"0004") == 0)SerialBT.println(power1);    //current_power
    else if (strcmp (buff,"0005") == 0)SerialBT.println(deadtime1);       //dead time
    else if (strcmp (buff,"0006") == 0)SerialBT.println(sourcecurrent1);       //source current
    else if (strcmp (buff,"0007") == 0)SerialBT.println(sinkcurrent1);       //sink current
    else if (strcmp (buff,"0008") == 0)SerialBT.println(distance1);       //distance covered
    else if (strcmp (buff,"1101") == 0)deadtime1="010.00";
    else if (strcmp (buff,"1102") == 0)deadtime1="050.00";
    else if (strcmp (buff,"1103") == 0)deadtime1="100.00";
    else if (strcmp (buff,"1201") == 0)sourcecurrent1="0500.00";
    else if (strcmp (buff,"1202") == 0)sourcecurrent1="1000.00";
    else if (strcmp (buff,"1203") == 0)sourcecurrent1="1500.00";
    else if (strcmp (buff,"1301") == 0)sinkcurrent1="1000.00";
    else if (strcmp (buff,"1302") == 0)sinkcurrent1="1500.00";
    else if (strcmp (buff,"1303") == 0)sinkcurrent1="2000.00";   
    else
    {
    Serial.write(65);   
    }
 }

  delay(20);
}
