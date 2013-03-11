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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for power utilities
 * @author Christe, Anthony
 */
public class PowerUtilsTest {
    public PowerUtilsTest() {
    }

    /**
     * Test of getWatts method, of class PowerUtils.
     */
    @Test
    public void testGetWatts() {
        System.out.println("getWatts");
        double voltage = 400.5;
        double current = 11.2;
        double expResult = 4485.6;
        double result = PowerUtils.getWatts(voltage, current);
        assertEquals(expResult, result, 0.5);
    }

    /**
     * Test of getCurrentCh1 method, of class PowerUtils.
     */
    @Test
    public void testGetCurrent() {
        System.out.println("getCurrent");
        int curPk = 712;
        double expResult = 9.27;
        double result = PowerUtils.getCurrentCh1(curPk);
        assertEquals(expResult, result, 0.5);
    }

    /**
     * Test of voltageTransform method, of class PowerUtils.
     */
    /*
    @Test
    public void testVoltageTransform() {
        System.out.println("voltageTransform");
        int[] samples = null;
        double[] expResult = null;
        double[] result = PowerUtils.voltageTransform(samples);
        //assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    */
}
