/*
	This file is part of KIP.
    KIP is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    KIP is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
*/
#ifndef KIP_GLOBALS
#define KIP_GLOBALS

#include <inttypes.h>
#include <avr/io.h>
#include <avr/interrupt.h>

///CPU Speed 8Mhz.
#define F_CPU 8000000UL

#include <util/delay.h>

///LED PIN GREEN.
#define LED_GREEN PD0
///LED PIN RED.
#define LED_RED PD1

///EEPROM config structure.
struct config_struct {
    ///Is the configuration valid.
    uint8_t programed;
    ///Mac address to use.
    uint8_t mac[6];
    ///IP address to use.
    uint8_t ip[4];
    ///Port to respond from.
    uint16_t port;
    ///ID of the device.
    uint8_t id;
    ///Delay of sampling time.
    uint16_t sampleDelay;
};

#define CONFIG_VALID 0xCD

///Configuration structure.
extern struct config_struct config_object;

///Load the config from the eeprom.
void load_config();

///Save the config from the eeprom.
void store_config();
#endif
