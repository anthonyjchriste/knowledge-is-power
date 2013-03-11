/*
 * Copyright 2012 Christe, Anthony
 * 
 * This file is part of KiP.
 *
 * KiP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * KiP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with KiP.  If not, see <http://www.gnu.org/licenses/>.
 */

package kip.client;

import java.util.Calendar;
import kip.utils.Constants;
import kip.utils.PacketUtils;
import kip.utils.PowerUtils;

/**
 * Provides access to data stored in UDP packets
 * @author Christe, Anthony
 */
public class Packet implements Comparable<Packet> {
    /**
     * Voltage data source
     */
    public static final int VOLTAGE = 0;
    
    /**
     * Current channel 1 data source
     */
    public static final int CURRENT_CHANNEL_1 = 1;
    
    /**
     * Current channel 2 data source
     */
    public static final int CURRENT_CHANNEL_2 = 2;
    
    private Calendar timeStamp = null;
    private byte[] data;
    private int[][] samples;
    private int Vpk, curCh1Pk, curCh2Pk;
    private int delay = 0;
    private double currentCh1, currentCh2, Vrms, watts, freqCh1, freqCh2;
    private double[] fft;
    
    /**
     * Constructs a new packet object
     * @param timeStamp time that packet was received
     * @param data contents of datagram
     */
    public Packet(Calendar timeStamp, byte[] data) {
        this.timeStamp = timeStamp;
        this.data = data.clone();
        this.samples = PacketUtils.getDACMeasurements(this.data);
        this.fft = PowerUtils.voltageTransform(samples[VOLTAGE]);
        this.Vpk = PacketUtils.getMaxDACValue(data, VOLTAGE);
        this.curCh1Pk = PacketUtils.getMaxDACValue(data, CURRENT_CHANNEL_1);
        this.curCh2Pk = PacketUtils.getMaxDACValue(data, CURRENT_CHANNEL_2);
        this.currentCh1 = PowerUtils.getCurrentCh1(curCh1Pk);
        this.currentCh2 = PowerUtils.getCurrentCh2(curCh2Pk);
        this.Vrms = this.Vpk / Constants.SQRT_OF_2;
        this.watts = PowerUtils.getWatts(this.Vpk / Constants.SQRT_OF_2,
                                         this.currentCh1);
        this.freqCh1 = PowerUtils.getFrequency(samples[CURRENT_CHANNEL_1]);
        this.freqCh2 = PowerUtils.getFrequency(samples[CURRENT_CHANNEL_2]);
        this.delay = this.delay  | (data[Constants.SERVER_DELAY_INDEX_HIGH] & 0xFF);
        this.delay = this.delay << 8;
        this.delay = this.delay | (data[Constants.SERVER_DELAY_INDEX_LOW] & 0xFF);
    }
    
    /**
     * Return data stored in packet
     * @return data stored in packet
     */
    public byte[] getData() {
        return this.data.clone();
    }
    
    /**
     * Get either the voltage, current channel 1, or current channel 2 samples
     * @param channel   specifies the data source do you want samples from
     * @return          samples for specified data source
     */
    public int[] getSamples(final int channel) {
        switch (channel) {
            case VOLTAGE:
                return samples[VOLTAGE];
            case CURRENT_CHANNEL_1:
                return samples[CURRENT_CHANNEL_1];
            case CURRENT_CHANNEL_2:
                return samples[CURRENT_CHANNEL_2];
            default:
                return new int[0];
        }
    }
    
    /**
     * Returns samples from all data sources as a 2D array
     * 
     * Each row contains a different data source. Row 0 contains voltage samples,
     * row 2 contains current channel 1 samples, and row 3 contains current 
     * channel 2 samples
     * @return samples for all data sources
     */
    public int[][] getSamples() {
        return this.samples.clone();
    }
    
    /**
     * Get the voltage peak
     * @return voltage peak of packet
     */
    public int getVpk() {
        return this.Vpk;
    }
    
    /**
     * Get voltage root means squared
     * @return voltage root means squared
     */
    public double getVrms() {
        return Constants.VOLTAGE;
    }
    
    /**
     * Get the frequency for current channel 1
     * @return frequency of current channel 1
     */
    public double getFreqCh1() {
        return freqCh1;
    }
    
    /**
     * Get the frequency for current channel 2
     * @return frequency of current channel 2
     */
    public double getFreqCh2() {
        return freqCh2;
    }
    
    /**
     * Get current channel 1 peak
     * @return current channel 1 peak
     */
    public int getCurCh1PK() {
        return this.curCh1Pk;
    }

    /**
     * Get current channel 2 peak
     * @return current channel 2 peak
     */
    public int getCurCh2PK() {
        return this.curCh2Pk;
    }
    
    /**
     * Gets the current for channel 1
     * @return the current ch 1 (amps)
     */
    public double getCurrentCh1() {
        return this.currentCh1;
    }
    
    /**
     * Gets the current for channel 2
     * @return the current ch 2 (amps)
     */
    public double getCurrentCh2() {
        return this.currentCh2;
    }

    /**
     * Gets the watts
     * @return watts
     */
    public double getWatts() {
        return this.watts;
    }

    /**
     * Get the board delay
     * @return the board delay in milliseconds
     */
    public int getDelay() {
        return delay;
    }
    
    /**
     * Gets the time stamp
     * @return packet's time stamp
     */
    public Calendar getTimeStamp() {
        return this.timeStamp;
    }
    
    /**
     * Gets the year from the time stamp
     * @return year 
     */
    public int getYear() {
        return timeStamp.get(Calendar.YEAR);
    }

    /**
     * Gets the month from the time stamp
     * @return month
     */
    public int getMonth() {
        return timeStamp.get(Calendar.MONTH);
    }
    
    /**
     * Gets the day from the time stamp
     * @return day
     */
    public int getDay() {
        return timeStamp.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Gets the hour from the time stamp
     * @return hour
     */
    public int getHour() {
        return timeStamp.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Gets the minute from the time stamp
     * @return minute
     */
    public int getMinute() {
        return timeStamp.get(Calendar.MINUTE);
    }

    /**
     * Gets the second from the time stamp
     * @return second
     */
    public int getSecond() {
        return timeStamp.get(Calendar.SECOND);
    }

    /**
     * Gets the milliseconds from the time stamp
     * @return milliseconds
     */
    public long getMilliSeconds() {
        return timeStamp.get(Calendar.MILLISECOND);
    }

    /**
     * Gets the time in milliseconds since the epoch
     * @return milliseconds since the epoch
     */
    public long getEpoch() {
        return this.timeStamp.getTimeInMillis();
    }

    @Override
    public String toString() {
        return Long.toString(getMilliSeconds());
    }
    
    @Override
    public int compareTo(Packet packet) {
        return this.timeStamp.compareTo(packet.getTimeStamp());
    }
}
