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

#include "adc_read.h"
#include <avr/io.h> 
#include "config.h"


uint8_t buff[ADC_PACKET_SIZE];

#define CHECKSUM_OFFSET 400

#define ENABLE_ADC    	ADCSRA |= (1 << ADEN)
#define DISABLE_ADC		ADCSRA &= (1 << ADEN)
#define ADC_START		ADCSRA |= (1 << ADSC)
#define ADC_WAIT	    while(ADCSRA & (1<<ADSC))


inline static void checksum(void)
{
	uint16_t i;
	uint8_t accum = 0;
	for(i = 0; i< ADC_PACKET_SIZE; i++)
	{
		if(i == CHECKSUM_OFFSET)
			continue;
		accum += buff[i];
	}
	buff[CHECKSUM_OFFSET] = accum;
	return;
}

static uint16_t adc_read_chan(uint8_t ch)
{
	//Clear top 5 bits of input.
    ch &= 0x7;
    //Clear the bottom 3 bits of the mux. 						
    //ADMUX = (ADMUX & 0xF8)|ch;		
        switch (ch) {
        case 0://binary 0 (reading downwards)
            ADMUX &= ~(1 << MUX0)
                  &  ~(1 << MUX1)
                  &  ~(1 << MUX2)
                  &  ~(1 << MUX3);
            break;
        case 1://binary 1
            ADMUX |=  (1 << MUX0);
            ADMUX &= ~(1 << MUX1)
                  &  ~(1 << MUX2)
                  &  ~(1 << MUX3);
            break;
        case 2://binary 2
            ADMUX &= ~(1 << MUX0);
            ADMUX |=  (1 << MUX1);
            ADMUX &= ~(1 << MUX2)
                  &  ~(1 << MUX3);
            break;
        case 3: //should have the picture by now
            ADMUX |=  (1 << MUX0)
                  |   (1 << MUX1);
            ADMUX &= ~(1 << MUX2)
                  &  ~(1 << MUX3);
            break;
        case 4:
            ADMUX &= ~(1 << MUX0)
                  &  ~(1 << MUX1);
            ADMUX |=  (1 << MUX2);
            ADMUX &= ~(1 << MUX3);
            break;
        case 5:
            ADMUX |=  (1 << MUX0);
            ADMUX &= ~(1 << MUX1);
            ADMUX |=  (1 << MUX2);
            ADMUX &= ~(1 << MUX3);
            break;
        case 6:
            ADMUX &= ~(1 << MUX0);
            ADMUX |=  (1 << MUX1)
                  |   (1 << MUX2);
            ADMUX &= ~(1 << MUX3);
            break;
        case 7:
            ADMUX |=  (1 << MUX0)
                  |   (1 << MUX1)
                  |   (1 << MUX2);
            ADMUX &= ~(1 << MUX3);
            break;
        case 8:
            ADMUX &= ~(1 << MUX0)
                  &  ~(1 << MUX1)
                  &  ~(1 << MUX2);
            ADMUX |=  (1 << MUX3);
            break;
    }
	ADC_START;
	ADC_WAIT;
    return (ADC);
}

void init_adc()
{
	// Set ADC reference to AVCC 
	ADMUX = (1 << REFS0); 									
	//62KHz sample rate @ 8MHz 	
	ADCSRA = (1<<ADEN)|(1<<ADPS2)|(1<<ADPS1)|(1<<ADPS0);		
}

uint8_t* adc_read()
{
	uint16_t i;
	uint16_t sleep;
	uint16_t adc;
	uint8_t *adc_buff;
	
	//Fill in the data packet fields.
	buff[0] = 0xBF;
	buff[1] = config_object.id;
	buff[386] = config_object.sampleDelay >> 8;
    buff[387] = config_object.sampleDelay & 0xFF;
	buff[401] = 0xFB;
	adc_buff = (uint8_t*)(buff+2);
	ENABLE_ADC;	//Save some power.
	for(i = 0; i < 64; i++)
	{
		adc= adc_read_chan(1);
		adc_buff[i*6] = adc >> 8;
		adc_buff[i*6 +1] = adc & 0xFF;
		adc= adc_read_chan(0);
                adc_buff[i*6+2] = adc >> 8;
                adc_buff[i*6 +3] = adc & 0xFF;
		adc= adc_read_chan(7);
                adc_buff[i*6 + 4] = adc >> 8;
                adc_buff[i*6 + 5] = adc & 0xFF;
        for(sleep = 0; sleep < config_object.sampleDelay; sleep++)
			_delay_ms(1);
	}
	
	//DISABLE_ADC;
	checksum();
	return buff;
}
