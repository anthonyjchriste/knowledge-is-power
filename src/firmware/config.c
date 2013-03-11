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

#include "config.h"
#include <avr/eeprom.h>

///Offset to the config structure in the eeprom.
#define CONFIG_EEPROM_OFFSET 4

///Global config structure. Life is so nice when your code is single threaded.
struct config_struct config_object;

//Load the configuration from eeprom
void load_config()
{
    eeprom_read_block((void*)&config_object, (const void*)CONFIG_EEPROM_OFFSET, sizeof(struct config_struct));
    if(config_object.programed != CONFIG_VALID)
    {
        config_object.id = 1;
        config_object.ip[0] = 192;
        config_object.ip[1] = 168;
        config_object.ip[2] = 1;
        config_object.ip[3] = 151;

        config_object.mac[0] = 0x02;
        config_object.mac[1] = 0x01;
        config_object.mac[2] = 0x01;
        config_object.mac[3] = 0x01;
        config_object.mac[4] = 0x01;
        config_object.mac[5] = 0x01;

        config_object.port = 10001;
        config_object.sampleDelay = 0x00;
        config_object.programed = CONFIG_VALID;
    }
}

//Save configuration to the eeprom
void store_config()
{
    if(config_object.programed != CONFIG_VALID)
        return;
    eeprom_write_block((void*)&config_object, (void*)CONFIG_EEPROM_OFFSET, sizeof(struct config_struct));
}
