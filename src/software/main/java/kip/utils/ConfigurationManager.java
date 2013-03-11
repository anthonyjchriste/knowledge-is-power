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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Read and parse KiP configuration file.
 * @author Christe, Anthony
 */
public class ConfigurationManager {
    // Configuration properties
    public static final int REAL_TIME_REFRESH = 0;
    public static final int REAL_TIME_BUFFER = 1;
    public static final int DEFAULT_BOARD_IP = 2;
    public static final int DEFAULT_BOARD_PORT = 3;
    public static final int DEFAULT_BOARD_ID = 4;
    public static final int DEFAULT_BOARD_POLL = 5;
    public static final int DEFAULT_BOARD_DURATION = 6;
    public static final int DEFAULT_BOARD_MAC = 7;
    public static final int DEBUG = 8;
    
    private static HashMap<Integer, String> configuration;
    private static HashMap<String, Integer> settingToInt;
    private static boolean debug = false;

    /**
     * Set up default configuration options of configuration file is not present.
     * 
     * Also set initialize mappings for settings.
     */
    public static void init() {
        configuration = new HashMap<Integer, String>();
        settingToInt = new HashMap<String, Integer>(10);

        settingToInt.put("REAL_TIME_REFRESH", REAL_TIME_REFRESH);
        settingToInt.put("REAL_TIME_BUFFER", REAL_TIME_BUFFER);
        settingToInt.put("DEFAULT_BOARD_IP", DEFAULT_BOARD_IP);
        settingToInt.put("DEFAULT_BOARD_PORT", DEFAULT_BOARD_PORT);
        settingToInt.put("DEFAULT_BOARD_ID", DEFAULT_BOARD_ID);
        settingToInt.put("DEFAULT_BOARD_POLL", DEFAULT_BOARD_POLL);
        settingToInt.put("DEFAULT_BOARD_DURATION", DEFAULT_BOARD_DURATION);
        settingToInt.put("DEFAULT_BOARD_MAC", DEFAULT_BOARD_MAC);
        settingToInt.put("DEBUG", DEBUG);
        
        configuration.put(REAL_TIME_REFRESH, "2");
        configuration.put(REAL_TIME_BUFFER, "10");
        configuration.put(DEFAULT_BOARD_IP, "");
        configuration.put(DEFAULT_BOARD_PORT, "");
        configuration.put(DEFAULT_BOARD_ID, "");
        configuration.put(DEFAULT_BOARD_POLL, "");
        configuration.put(DEFAULT_BOARD_DURATION, "");
        configuration.put(DEFAULT_BOARD_MAC, "");
        configuration.put(DEBUG, "0");
    }

    /**
     * Initializes the configuration
     * @param configLoc the location of the configuration file
     */
    public static void init(String configLoc) {
        init();
        readConfigurationFile(configLoc);
    }

    /**
     * Read and parse configuration file.
     * 
     * There is basic support for debugging if an error is encountered, we can
     * at least give you the line number the error occured on.
     * @param configLoc 
     */
    private static void readConfigurationFile(String configLoc) {
        String line;
        String[] splitLine;
        int lineCnt = 1;

        try {
            Scanner scan = new Scanner(new File(configLoc));
            while (scan.hasNextLine()) {
                line = scan.nextLine();
                // Ignore empty lines of lines with comments
                if (line.length() > 0 && !line.substring(0, 1).equals("#")) {
                    splitLine = line.split(" ");
                    if (settingToInt.containsKey(splitLine[0])) {
                        switch (settingToInt.get(splitLine[0])) {
                            case REAL_TIME_REFRESH:
                                configuration.put(REAL_TIME_REFRESH,
                                                  splitLine[1]);
                                break;
                            case REAL_TIME_BUFFER:
                                configuration.put(REAL_TIME_BUFFER, splitLine[1]);
                                break;
                            case DEFAULT_BOARD_IP:
                                configuration.put(DEFAULT_BOARD_IP, splitLine[1]);
                                break;
                            case DEFAULT_BOARD_PORT:
                                configuration.put(DEFAULT_BOARD_PORT,
                                                  splitLine[1]);
                                break;
                            case DEFAULT_BOARD_ID:
                                configuration.put(DEFAULT_BOARD_ID, splitLine[1]);
                                break;
                            case DEFAULT_BOARD_POLL:
                                configuration.put(DEFAULT_BOARD_POLL,
                                                  splitLine[1]);
                                break;
                            case DEFAULT_BOARD_DURATION:
                                configuration.put(DEFAULT_BOARD_DURATION,
                                                  splitLine[1]);
                                break;
                            case DEFAULT_BOARD_MAC:
                                configuration.put(DEFAULT_BOARD_MAC, splitLine[1]);
                                break;
                            case DEBUG:
                                configuration.put(DEBUG, splitLine[1]);
                                if(Integer.parseInt(splitLine[1]) == 1) 
                                    debug = true;
                                break;
                            default:
                                System.err.println(
                                        "Invalid configuration file @ " + lineCnt);
                        }
                    }
                }
                lineCnt++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Configuration file not found. Using defaults");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Invalid configuration file @ " + lineCnt);
        }
    }
    
    /**
     * Sets a configurate property
     * @param property  the property being set
     * @param value     the value to change the property to
     */
    public static void setProperty(final int property, String value) {
        if (property < configuration.size()) {
            configuration.put(property, value);
        }
    }

    /**
     * Gets the value of a selected property
     * @param property  the property we wish to inspect
     * @return          the value of the property we're inspecting
     */
    public static String getProperty(final int property) {
        if (property < configuration.size()) {
            return configuration.get(property);
        } else {
            return "";
        }
    }
    
    /**
     * Test if debugging was set in configuration file
     * @return boolean value on whether or not debugging is turned on
     */
    public static boolean debug() {
        return debug;
    }
}
