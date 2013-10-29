package net.cubespace.RegionShop.Util;

import java.io.*;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 27.10.13 18:27
 */
public abstract class CSVReader {
    /**
     * Convert a InputStream gets converted to a String array
     * Each line gets splitted by ; and the onLine(String[] line) function
     * gets called each line found.
     *
     * @param inputStream The CSV InputStream which should be parsed
     */
    public CSVReader(InputStream inputStream) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String curLine;
            while((curLine = bufferedReader.readLine()) != null) {
                Logger.debug("CSV Reader - Raw Line: " + curLine);
                onLine(curLine.split("\\;"));
            }
        } catch (Exception e) {
            Logger.error("Could not read CSV File", e);
        }
    }

    /**
     * This function gets called every Line in the CSV file
     *
     * @param line The ; splitted line
     */
    public abstract void onLine(String[] line);
}
