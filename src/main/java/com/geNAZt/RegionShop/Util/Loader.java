package com.geNAZt.RegionShop.Util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.06.13
 */
public class Loader {
    @SuppressWarnings("ConstantConditions")
    public static <T> CopyOnWriteArrayList<T> loadFromJAR(String path, Class interf, Object[] init) {
        CopyOnWriteArrayList<T> returnObjects = new CopyOnWriteArrayList<T>();

        try {
            String pathToJar = Loader.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            JarFile jarFile = new JarFile(pathToJar);
            Enumeration e = jarFile.entries();

            ClassLoader cl = Loader.class.getClassLoader();

            while (e.hasMoreElements()) {
                JarEntry je = (JarEntry) e.nextElement();

                if(je.isDirectory() || !je.getName().endsWith(".class") || !je.getName().substring(0,je.getName().length()-6).replace("/", ".").contains(path +".")){
                    continue;
                }

                try {
                    String className = je.getName().substring(0,je.getName().length()-6);
                    className = className.replace('/', '.');
                    Class<?> c = cl.loadClass(className);

                    if(!interf.isAssignableFrom(c)) {
                        continue;
                    }

                    if(Modifier.isAbstract(c.getModifiers())) {
                        continue;
                    }

                    ArrayList<Class> classList = new ArrayList<Class>();
                    for(Object o : init) {
                        classList.add(o.getClass());
                    }

                    Constructor constructor = c.getConstructor(classList.toArray(new Class[]{}));

                    returnObjects.add((T)constructor.newInstance(init));
                } catch(ClassNotFoundException er) {
                    er.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }

            }
        } catch(IOException e) {
            e.printStackTrace();

            return null;
        }

        return returnObjects;
    }

    public static <T> CopyOnWriteArrayList<T> loadFromJAR(String path, Class interf) {
        CopyOnWriteArrayList<T> returnObjects = new CopyOnWriteArrayList<T>();

        try {
            String pathToJar = Loader.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            JarFile jarFile = new JarFile(pathToJar);
            Enumeration e = jarFile.entries();

            ClassLoader cl = Loader.class.getClassLoader();

            while (e.hasMoreElements()) {
                JarEntry je = (JarEntry) e.nextElement();

                if(je.isDirectory() || !je.getName().endsWith(".class") || !je.getName().substring(0,je.getName().length()-6).replace("/", ".").contains(path +".")){
                    continue;
                }

                try {
                    String className = je.getName().substring(0,je.getName().length()-6);
                    className = className.replace('/', '.');
                    Class<?> c = cl.loadClass(className);

                    if(!interf.isAssignableFrom(c)) {
                        continue;
                    }

                    if(Modifier.isAbstract(c.getModifiers())) {
                        continue;
                    }

                    Constructor constructor = c.getConstructor();
                    returnObjects.add((T)constructor.newInstance());
                } catch(ClassNotFoundException er) {
                    er.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }

            }
        } catch(IOException e) {
            e.printStackTrace();

            return null;
        }

        return returnObjects;
    }
}
