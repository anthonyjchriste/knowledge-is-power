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
#include "contrib/net.h"
#include "contrib/enc28j60.h"
#include "contrib/ip_arp_udp.h"
#include "adc_read.h"

#define BUFFER_SIZE 600

void init_leds()
{
    int i;
    DDRD |= (1 << LED_GREEN);
    DDRD |= (1 << LED_RED);
    PORTD |= 1 << LED_RED;
    for(i =0; i < 5; i++)
    {
        PORTD ^= 1 << LED_RED;
        PORTD ^= 1 << LED_GREEN;
        _delay_ms(500);
    }
}

void init_mac()
{
    enc28j60Init(config_object.mac);
    _delay_ms(100);
    /* Magjack leds configuration, see enc28j60 datasheet, page 11 */
    enc28j60PhyWrite(PHLCON,0x476);
    /* set output to GND, red LED on */
    PORTD |= 1 << LED_GREEN;
	init_ip_arp_udp(config_object.mac,config_object.ip);
}

uint8_t handle_ping_arp(uint8_t *buf, uint16_t plen)
{
    // arp is broadcast if unknown but a host may also
    // verify the mac address by sending it to
    // a unicast address.
    if(eth_type_is_arp_and_my_ip(buf,plen)) {
        make_arp_answer_from_request(buf,plen);
        return 1;
    }
    // check if ip packets (icmp or udp) are for us:
    if(eth_type_is_ip_and_my_ip(buf,plen)==0) {
        return 1;
    }
    if(buf[IP_PROTO_P]==IP_PROTO_ICMP_V && buf[ICMP_TYPE_P]==ICMP_TYPE_ECHOREQUEST_V) {
        // a ping packet, let's send pong
        make_echo_reply_from_request(buf,plen);
        return 1;
    }
    return  0;
}

int main()
{
    uint8_t buf[BUFFER_SIZE+1];
    uint16_t plen;
    uint8_t payloadlen=0;
	uint8_t i;
	uint8_t* adc;
    init_leds();
	
    load_config();

	init_adc();
    _delay_ms(100);
    init_mac();
    _delay_ms(100);
	
    while(1)
    {
        // get the next new packet:
        plen = enc28j60PacketReceive(BUFFER_SIZE, buf);
        /*plen will ne unequal to zero if there is a valid
         * packet (without crc error) */
        if(plen==0)
        {
            continue;
        }
        if(handle_ping_arp(buf, plen))
            continue;
        if (buf[IP_PROTO_P]==IP_PROTO_UDP_V)
        {
            payloadlen=buf[UDP_LEN_L_P]-UDP_HEADER_LEN;
			if(payloadlen != 11) //Mallformed command.
				continue;
            if (buf[UDP_DATA_P]== 0xBC)
            {
				switch(buf[UDP_DATA_P + 1])
				{
					case 1:
						adc = adc_read();
						make_udp_reply_from_request(buf,adc,ADC_PACKET_SIZE ,config_object.port);
						PORTD ^= 1 << LED_RED;
						
					break;
					
					case 2:
						//set the id.
						config_object.id = buf[UDP_DATA_P + 2] ;
					break;
					
					case 3:
						//set ip.
						for(i= 0; i< 4; i++)
							config_object.ip[i] = buf[UDP_DATA_P + 3 +i];
					break;
					
					case 4:
						//set mac
						for(i= 0; i< 6; i++)
							config_object.mac[i] = buf[UDP_DATA_P + 3 +i];
					break;
	                case 5:
						//set sample delay
						config_object.sampleDelay = buf[UDP_DATA_P + 3 +1] + ((uint8_t)(buf[UDP_DATA_P + 3]) << 8);
                    break;

					case 0xF:
						store_config();
						init_mac();
					break;
					
					default:
						//Mallformed command.
					break;
				}

            }
        }
    }
}
