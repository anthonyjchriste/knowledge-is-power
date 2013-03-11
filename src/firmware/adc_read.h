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

#ifndef KIP_ADC
#define KIP_ADC
#include "config.h"

///Size of the adc packet.
#define ADC_PACKET_SIZE 402

///Initialize adc.
void init_adc(void);

///Read adc.
uint8_t* adc_read(void);

#endif
