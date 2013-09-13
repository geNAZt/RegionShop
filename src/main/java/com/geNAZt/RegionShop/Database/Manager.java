package com.geNAZt.RegionShop.Database;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.LogLevel;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.SQLitePlatform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.RegionShopPlugin;

import javax.persistence.PersistenceException;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 26.06.13
 */
public class Manager {
    private RegionShopPlugin plugin;
    private HashMap<Class<?>, Boolean> databaseModels = new HashMap<Class<?>, Boolean>();
    private EbeanServer dbs;

    public Manager(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void addModel(Class<?> databaseModel, Boolean hasTable) {
        databaseModels.put(databaseModel, hasTable);
    }

    public void addModel(Class<?> databaseModel) {
       addModel(databaseModel, true);
    }

    private void installDDL() {
        SpiEbeanServer serv = (SpiEbeanServer)this.dbs;
        DdlGenerator gen = serv.getDdlGenerator();

        String createSQL = gen.generateCreateDdl();

        if (serv.getDatabasePlatform().getName().contains("sqlite")) {
            createSQL = validateCreateDDLSqlite(createSQL);
        }

        gen.runScript(false, createSQL);
    }

    private String validateCreateDDLSqlite(String oldScript) {
        try {
            //Create a BufferedReader out of the potentially invalid script
            BufferedReader scriptReader = new BufferedReader(new StringReader(oldScript));

            //Create an array to store all the lines
            List<String> scriptLines = new ArrayList<String>();

            //Create some additional variables for keeping track of tables
            HashMap<String, Integer> foundTables = new HashMap<String, Integer>();
            String currentTable = null;
            int tableOffset = 0;

            //Loop through all lines
            String currentLine;
            while ((currentLine = scriptReader.readLine()) != null) {
                //Trim the current line to remove trailing spaces
                currentLine = currentLine.trim();

                //Add the current line to the rest of the lines
                scriptLines.add(currentLine.trim());

                //Check if the current line is of any use
                if(currentLine.startsWith("create table")) {
                    //Found a table, so get its name and remember the line it has been encountered on
                    currentTable = currentLine.split(" ", 4)[2];
                    foundTables.put(currentLine.split(" ", 3)[2], scriptLines.size() - 1);
                }
                else if(currentLine.startsWith(";") && currentTable != null && !currentTable.equals("")) {
                    //Found the end of a table definition, so update the entry
                    int index = scriptLines.size() - 1;
                    foundTables.put(currentTable, index);

                    //Remove the last ")" from the previous line
                    String previousLine = scriptLines.get(index - 1);
                    previousLine = previousLine.substring(0, previousLine.length() - 1);
                    scriptLines.set(index - 1, previousLine);

                    //Change ";" to ");" on the current line
                    scriptLines.set(index, ");");

                    //Reset the table-tracker
                    currentTable = null;
                }
                else if(currentLine.startsWith("alter table")) {
                    //Found a potentially unsupported action
                    String[] alterTableLine = currentLine.split(" ", 4);

                    if(alterTableLine[3].startsWith("add constraint")) {
                        //Found an unsupported action: ALTER TABLE using ADD CONSTRAINT
                        String[] addConstraintLine = alterTableLine[3].split(" ", 4);

                        //Check if this line can be fixed somehow
                        if(addConstraintLine[3].startsWith("foreign key")) {
                            //Calculate the index of last line of the current table
                            int tableLastLine = foundTables.get(alterTableLine[2]) + tableOffset;

                            //Add a "," to the previous line
                            scriptLines.set(tableLastLine - 1, scriptLines.get(tableLastLine - 1) + ",");

                            //Add the constraint as a new line - Remove the ";" on the end
                            String constraintLine = String.format("%s %s %s", addConstraintLine[1], addConstraintLine[2], addConstraintLine[3]);
                            scriptLines.add(tableLastLine, constraintLine.substring(0, constraintLine.length() - 1));

                            //Remove this line and raise the table offset because a line has been inserted
                            scriptLines.remove(scriptLines.size() - 1);
                            tableOffset++;
                        }
                        else {
                            //Exception: This line cannot be fixed but is known the be unsupported by SQLite
                            throw new RuntimeException("Unsupported action encountered: ALTER TABLE using ADD CONSTRAINT with " + addConstraintLine[3]);
                        }
                    }
                }
            }

            //Turn all the lines back into a single string
            String newScript = "";
            for(String newLine : scriptLines) {
                newScript += newLine + "\n";
            }

            //Return the fixed script
            return newScript;
        }
        catch (Exception ex) {
            //Exception: Failed to fix the DDL or something just went plain wrong
            throw new RuntimeException("Failed to validate the CreateDDL-script for SQLite", ex);
        }
    }

    private boolean checkDDL() {
        try {
            for(Map.Entry<Class<?>, Boolean> databaseModel : this.databaseModels.entrySet()) {
                if(databaseModel.getValue()) {
                    this.dbs.find(databaseModel.getKey()).findRowCount();
                }
            }

            this.dbs.runCacheWarming();
        } catch (PersistenceException ex) {
            return false;
        }

        return true;
    }

    public EbeanServer createDatabaseConnection() {
        ServerConfig db = new ServerConfig();
        db.setDefaultServer(false);
        db.setRegister(false);

        ArrayList<Class<?>> databaseModels = new ArrayList<Class<?>>();
        for(Map.Entry<Class<?>, Boolean> databaseModel : this.databaseModels.entrySet()) {
            databaseModels.add(databaseModel.getKey());
        }

        db.setClasses(databaseModels);
        db.setName("RegionShop");

        DataSourceConfig ds = new DataSourceConfig();
        ds.setDriver(ConfigManager.main.DB_driver);
        ds.setUrl(ConfigManager.main.DB_url.replaceAll("\\{DIR\\}", plugin.getDataFolder().getPath().replaceAll("\\\\", "/") + "/"));
        ds.setUsername(ConfigManager.main.DB_username);
        ds.setPassword(ConfigManager.main.DB_password);
        ds.setIsolationLevel(Transaction.SERIALIZABLE);
        ds.setMaxConnections(ConfigManager.expert.DB_maxConnections);

        if (ds.getDriver().contains("sqlite")) {
            db.setDatabasePlatform(new SQLitePlatform());
            db.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
        }

        db.setDataSourceConfig(ds);

        ClassLoader previous = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(Manager.class.getClassLoader());
        this.dbs = EbeanServerFactory.create(db);
        Thread.currentThread().setContextClassLoader(previous);

        if(!checkDDL()) installDDL();

        this.dbs.runCacheWarming();

        return dbs;
    }
}
