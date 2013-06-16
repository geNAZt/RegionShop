package com.geNAZt.RegionShop.Util;

import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.06.13
 */
public class Loader {
    public static ArrayList<Object> loadFromJAR(Plugin plugin, String path, Class interf) {
        ArrayList<Object> returnObjects = new ArrayList<Object>();

        try {
            String pathToJar = Loader.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            JarFile jarFile = new JarFile(pathToJar);
            Enumeration e = jarFile.entries();

            URL[] urls = { new URL("jar:file:" + pathToJar +"!/") };
            ClassLoader cl = URLClassLoader.newInstance(urls);

            while (e.hasMoreElements()) {
                JarEntry je = (JarEntry) e.nextElement();

                if(je.isDirectory() || !je.getName().endsWith(".class") || !je.getName().contains(path)){
                    continue;
                }

                try {
                    String className = je.getName().substring(0,je.getName().length()-6);
                    className = className.replace('/', '.');
                    final Class<?> c = cl.loadClass(className);

                    if(!interf.isAssignableFrom(c)) {
                        continue;
                    }

                    Constructor[] cons = c.getConstructors();

                    for(Constructor con : cons) {
                        returnObjects.add(con.newInstance(plugin));
                        break;
                    }
                } catch(ClassNotFoundException er) {
                    er.printStackTrace();
                    continue;
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                    continue;
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                    continue;
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                    continue;
                }

            }
        } catch(IOException e) {
            e.printStackTrace();

            return null;
        }

        return returnObjects;
    }
}
