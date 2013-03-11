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

package kip.utils;

import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import java.util.ArrayList;

/**
 * Provides methods for analyzing and calculating power measurements.
 *
 * @author Christe, Anthony
 */
public class PowerUtils {
    /**
     * Slope of function for linear approximation of current
     */
    private static final double SLOPE_CH_1 = 0.00785;
    private static final double SLOPE_CH_2 = 0.085;
    
    /**
     * Calculate watts
     *
     * @param voltage the voltage of the power
     * @param current the current of the power
     * @return the total amount of watts
     */
    public static double getWatts(double voltage, double current) {
        return Constants.VOLTAGE * current;
    }

    /**
     * Calculate the current
     *
     * @param curPk the current peak
     * @return the current
     */
    public static double getCurrentCh1(int curPk) {
        return SLOPE_CH_1 * curPk;
    }
    
    /**
     * Return the current from channel 2
     * @param curPk the peak of current channel 2
     * @return      the current for channel 2
     */
    public static double getCurrentCh2(int curPk) {
        return SLOPE_CH_2 * curPk;
    }

    /**
     * Calculates the frequency from a packet
     * @param samples   Samples to calculate frequency on
     * @return          the frequency of the power
     */
    public static double getFrequency(int[] samples) {
        ArrayList<Integer> maximas = new ArrayList<Integer>();
        for (int i = 2; i < samples.length - 2; i++) 
        {
            if ((samples[i] >= samples[i + 1]) && (samples[i] >= samples[i - 1]))
				if ((samples[i] > samples[i + 2]) && (samples[i] > samples[i - 2])) 
				{
					if(samples[i] > 4)
					{
						if(maximas.size() > 0)
						{
							if( i - maximas.get(maximas.size() -1) > 5)
							{
								maximas.add(i);
							}
						}
						else
						{
							maximas.add(i);
						}
					}

				}
        }
        if (maximas.size() < 2) 
        {
            return 0.0;
        }
        return 1500*(1.0 / (maximas.get(1) - maximas.get(0)));
    }

    /**
     * Performs a FFT over the data
     * @param samples   samples to perform FFT over
     * @return          transformed voltage
     */
    public static double[] voltageTransform(int[] samples) {
        double[][] data = new double[2][samples.length];

        for (int i = 0; i < data[0].length; i++) {
            data[0][i] = i;
            data[1][i] = 0;
        }

        FastFourierTransformer.transformInPlace(data, DftNormalization.STANDARD,
                                                TransformType.FORWARD);
        //System.out.println(java.util.Arrays.toString(samples) + "\n");
        //System.out.println(java.util.Arrays.toString(data[0]) + "\n");
        return data[0];
    }
}
