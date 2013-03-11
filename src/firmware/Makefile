FILES= main.c config.c adc_read.c contrib/enc28j60.c contrib/ip_arp_udp.c
all: kip.hex

program: kip.hex
	@avrdude -c usbtiny -patmega328p -U lfuse:w:0xe2:m -U hfuse:w:0xd9:m -F
	@avrdude -c usbtiny  -patmega328p -U flash:w:kip.hex -F
	
kip.hex: $(FILES)
	@avr-gcc -g -Os -Wall -mcall-prologues -mmcu=atmega328p $(FILES) -o kip.hex
	@avr-objcopy -R .eeprom -O ihex kip.hex 
	

clean:
	rm -rf kip.hex
