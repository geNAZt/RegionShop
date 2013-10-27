package net.cubespace.RegionShop.Util;

import java.io.*;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 27.10.13 18:27
 */
public abstract class CSVReader {
    public CSVReader(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

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

    public abstract void onLine(String[] line);
}
