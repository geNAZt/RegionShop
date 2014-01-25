package net.cubespace.RegionShop.Util;

import java.io.*;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
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

            bufferedReader.close();

            onEnd();
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

    /**
     * This function gets called if the end of the CSV is reached
     */
    public abstract void onEnd();
}
