package net.cubespace.RegionShop.Config;

import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last modified 27.10.2013 11:31
 *
 * This abstract class is the part of the serialization from and to YAML
 */
public abstract class ConfigObject {
    /**
     * This function gets called from the Config save/load IO Handler if he wants to load a new Config
     *
     * @param cs The ConfigurationSection which has been loaded (The root one)
     * @throws Exception
     */
    protected void onLoad(ConfigurationSection cs) throws Exception {
        /* Go through all public Fields this class has
         * The mapping of variables to YAML will be _ => .
         *
         * So 'public Boolean Test_IsEnabled = true' will be
         * 'Test:
         *   IsEnabled: true'
         * in the YAML
         */
        for(Field field : getClass().getDeclaredFields()) {
            String path = field.getName().replaceAll("_", ".");

            //Transient, final and static or private Fields will be skipped
            if(!doSkip(field)) {
                if(cs.isSet(path)) {
                    //If the variable Path is in the YAML load the path and set the value
                    field.set(this, loadObject(field, cs, path));
                } else {
                    //If the variable is set in the Object but not in the YAML set it
                    cs.set(path, saveObject(field.get(this), field, cs, path));
                }
            }
        }
    }

    /**
     * This function gets called from the Config save/load IO Handler if he wants to save the Config
     *
     * @param cs The ConfigurationSection to save into
     * @throws Exception
     */
    protected void onSave(ConfigurationSection cs) throws Exception {
        /* Go through all public Fields this class has
         * The mapping of variables to YAML will be _ => .
         *
         * So 'public Boolean Test_IsEnabled = true' will be
         * 'Test:
         *   IsEnabled: true'
         * in the YAML
         */
        for(Field field : getClass().getDeclaredFields()) {
            String path = field.getName().replaceAll("_", ".");

            //Transient, final and static or private Fields will be skipped
            if(!doSkip(field)) {
                cs.set(path, saveObject(field.get(this), field, cs, path));
            }
        }
    }

    /**
     * This function loads a YAML path value into a Java Object field
     *
     * @param field The field which the value should be loaded into
     * @param cs The ConfigurationSection which holds the Path
     * @param path The YAML Path for the value to load from
     * @return A basic Java Object which can be set into a field
     * @throws Exception
     */
    protected Object loadObject(Field field, ConfigurationSection cs, String path) throws Exception {
        return loadObject(field, cs, path, 0);
    }

    /**
     * This function saves a Java Object field into a YAML Path
     *
     * @param obj The value which should be saved into the YAML ConfigurationSection
     * @param field The field which should be saved
     * @param cs The ConfigurationSection which will be used to save into
     * @param path The path in which the value should be saved
     * @return A basic Java Object which can be saved into the YAML ConfigurationSection
     * @throws Exception
     */
    protected Object saveObject(Object obj, Field field, ConfigurationSection cs, String path) throws Exception {
        return saveObject(obj, field, cs, path, 0);
    }

    /**
     * This function determinates which class the YAML Value has and load the correct Object for it
     *
     * @param field The field in which the Value should be loaded into
     * @param cs The ConfigurationSection which holds all Configuration Values
     * @param path The path from the YAML ConfigSection which the value should be loaded from
     * @param depth Only needed for recursive look into Classes (dont give any other value as 0 by yourself)
     * @return A basic Java Object which can be loaded into the Java Object Field
     * @throws Exception
     */
    protected Object loadObject(Field field, ConfigurationSection cs, String path, int depth) throws Exception {
        Class clazz = getClassAtDepth(field.getGenericType(), depth);

        if(ConfigObject.class.isAssignableFrom(clazz) && isConfigurationSection(cs.get(path))) {
            return getConfigObject(clazz, cs.getConfigurationSection(path));
        } else if(Map.class.isAssignableFrom(clazz) && isConfigurationSection(cs.get(path))) {
            return getMap(field, cs.getConfigurationSection(path), depth);
        } else if(clazz.isEnum() && isString(cs.get(path))) {
            return getEnum(clazz, (String) cs.get(path));
        } else if(List.class.isAssignableFrom(clazz) && isConfigurationSection(cs.get(path))) {
            Class subClazz = getClassAtDepth(field.getGenericType(), depth+1);

            if(ConfigObject.class.isAssignableFrom(subClazz) || Map.class.isAssignableFrom(subClazz) || List.class.isAssignableFrom(subClazz) || subClazz.isEnum()) {
                return getList(field, cs.getConfigurationSection(path), depth);
            } else {
                return cs.get(path);
            }
        } else {
            return cs.get(path);
        }
    }

    /**
     * This function saves an field Value as an Object into the YAML ConfigurationSection
     *
     * @param obj The object which should be saved
     * @param field The field from which the value should be saved from
     * @param cs The ConfigurationSection which holds all Configuration Values
     * @param path The path from the YAML ConfigSection which the value should be saved to
     * @param depth Only needed for recursive look into Classes (dont give any other value as 0 by yourself)
     * @return A basic Java Object which can be saved into the YAML ConfigurationSection
     * @throws Exception
     */
    protected Object saveObject(Object obj, Field field, ConfigurationSection cs, String path, int depth) throws Exception {
        Class clazz = getClassAtDepth(field.getGenericType(), depth);

        if(ConfigObject.class.isAssignableFrom(clazz) && isConfigObject(obj)) {
            return getConfigObject((ConfigObject) obj, path, cs);
        } else if(Map.class.isAssignableFrom(clazz) && isMap(obj)) {
            return getMap((Map) obj, field, cs, path, depth);
        } else if(clazz.isEnum() && isEnum(clazz, obj)) {
            return getEnum((Enum) obj);
        } else if(List.class.isAssignableFrom(clazz) && isList(obj)) {
            Class subClazz = getClassAtDepth(field.getGenericType(), depth+1);

            if(ConfigObject.class.isAssignableFrom(subClazz) || Map.class.isAssignableFrom(subClazz) || List.class.isAssignableFrom(subClazz) || subClazz.isEnum()) {
                return getList((List) obj, field, cs, path, depth);
            } else {
                return obj;
            }
        } else {
            return obj;
        }
    }

    /**
     * This function detects which class is the correct one for this Type
     *
     * @param type The type which should be checked
     * @param depth If there is a Class which has Parameterized Types in it you will need to go into the recursive depth of the classes
     * @return Class
     * @throws Exception
     */
    protected Class getClassAtDepth(Type type, int depth) throws Exception {
        if(depth<=0) {
            String className = type.toString();

            //If the Type is a class it will give you a string like "class java.util.ArrayList<net.cubespace.Example.Items>" if so strip away the "class "
            if(className.length() >= 6 && className.substring(0, 6).equalsIgnoreCase("class ")) {
                className = className.substring(6);
            }

            //Now you hav a String like "java.util.ArrayList<net.cubespace.Example.Items>". If the class contains a < it has some Parameterized Types in it. Only get the first Class
            if(className.contains("<")) {
                className = className.substring(0, className.indexOf("<"));
            }

            //Now you have the "java.util.ArrayList" class as String, search the Class in the classloader
            try {
                return Class.forName(className);
            } catch(ClassNotFoundException ex) {
                //Class not found => Check for primitive Types
                if(className.equalsIgnoreCase("byte")) return Byte.class;
                if(className.equalsIgnoreCase("short")) return Short.class;
                if(className.equalsIgnoreCase("int")) return Integer.class;
                if(className.equalsIgnoreCase("long")) return Long.class;
                if(className.equalsIgnoreCase("float")) return Float.class;
                if(className.equalsIgnoreCase("double")) return Double.class;
                if(className.equalsIgnoreCase("char")) return Character.class;
                if(className.equalsIgnoreCase("boolean")) return Boolean.class;

                Logger.fatal("Could not find Class for Type", ex);
            }
        }

        depth--;

        //If type has multiple Type Arguments, shift through all to get the right class
        ParameterizedType pType = (ParameterizedType) type;
        Type[] typeArgs = pType.getActualTypeArguments();
        return getClassAtDepth(typeArgs[typeArgs.length-1], depth);
    }

    /**
     * Check if Object is a String
     *
     * @param obj The Object to check for
     * @return boolean
     */
    protected boolean isString(Object obj) {
        return (obj instanceof String);
    }

    /**
     * Check if Object is a ConfigurationSection
     *
     * @param obj The Object to check for
     * @return boolean
     */
    protected boolean isConfigurationSection(Object obj) {
        return ConfigurationSection.class.isInstance(obj);
    }

    /**
     * Check if Object is a ConfigObject
     *
     * @param obj The Object to check for
     * @return boolean
     */
    protected boolean isConfigObject(Object obj) {
        return ConfigObject.class.isInstance(obj);
    }

    /**
     * Check if Object is a Map
     *
     * @param obj The Object to check for
     * @return boolean
     */
    protected boolean isMap(Object obj) {
        return Map.class.isInstance(obj);
    }

    /**
     * Check if Object is a List
     *
     * @param obj The Object to check for
     * @return boolean
     */
    protected boolean isList(Object obj) {
        return List.class.isInstance(obj);
    }

    /**
     * Check if Object is part of an Enum
     *
     * @param obj The Object to check for
     * @return boolean
     */
    protected boolean isEnum(Class clazz, Object obj) {
        //Check if given class is a valid enum
        if(!clazz.isEnum()) return false;

        //Check if the Object is part of this enum
        for(Object constant : clazz.getEnumConstants()) {
            if(constant.equals(obj)) {
                return true;
            }
        }

        return false;
    }

    /**
     * This function generates a new ConfigObject and loads the ConfigurationSection into it
     *
     * @param clazz The ConfigObject Class to create and load into
     * @param cs The ConfigurationObject to load into the ConfigObject
     * @return ConfigObject
     * @throws Exception
     */
    protected ConfigObject getConfigObject(Class clazz, ConfigurationSection cs) throws Exception {
        ConfigObject obj = (ConfigObject) clazz.newInstance();
        obj.onLoad(cs);
        return obj;
    }

    /**
     * This function generates a new HashMap and loads all value from the ConfigurationSection into it
     *
     * @param field The field which the HashMap should be loaded into
     * @param cs The ConfigurationSection which should be used to load from
     * @param depth Recursive loading from the ConfigurationSection
     * @return HashMap
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected Map getMap(Field field, ConfigurationSection cs, int depth) throws Exception {
        depth++;

        Set<String> keys = cs.getKeys(false);
        Map map = new HashMap();

        if(keys != null && keys.size() > 0) {
            for(String key : keys) {
                Object in = loadObject(field, cs, key, depth);
                map.put(key, in);
            }
        }

        return map;
    }

    /**
     * This function loads a ConfigurationSection which holds a list with another Typed Parameter in it
     *
     * @param field The field which should be loaded into
     * @param cs The ConfigurationSection which should be loaded form
     * @param depth Recursive for Typed Parameters
     * @return List
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected List getList(Field field, ConfigurationSection cs, int depth) throws Exception {
        depth++;

        Set<String> keys = cs.getKeys(false);
        List list = new ArrayList();

        if(keys != null && keys.size() > 0) {
            for(String key : keys) {
                Object in = loadObject(field, cs, key, depth);
                list.add(in);
            }
        }

        return list;
    }

    /**
     * This function gets the Enum of a class and a string
     *
     * @param clazz The class which the Enum should be
     * @param string The selected Enum from this class
     * @return Enum
     * @throws Exception
     */
    protected Enum getEnum(Class clazz, String string) throws Exception {
        if(!clazz.isEnum()) throw new Exception("Class " + clazz.getName() + " is not an enum.");

        for(Object constant : clazz.getEnumConstants()) {
            if(((Enum) constant).toString().equals(string)) {
                return (Enum) constant;
            }
        }

        throw new Exception("String " + string + " is not a valid enum constant for " + clazz.getName());
    }

    /**
     * This function takes an ConfigObject and saves it into a ConfigurationSection
     *
     * @param obj The ConfigObject to save
     * @param path The path in the ConfigurationObject which the object should be saved to
     * @param cs The ConfigurationSection which should be used to save into
     * @return ConfigurationSection
     * @throws Exception
     */
    protected ConfigurationSection getConfigObject(ConfigObject obj, String path, ConfigurationSection cs) throws Exception {
        ConfigurationSection subCS = cs.createSection(path);
        obj.onSave(subCS);
        return subCS;
    }

    /**
     * This function takes a Map and saves it into a ConfigurationSection
     *
     * @param map The map which should be saved
     * @param field The field from which the map comes from
     * @param cs The ConfigurationSection which should be used to save into
     * @param path The starting Path in the ConfigurationSection which should be used to save the Map
     * @param depth Recursive Typed Parameter
     * @return ConfigurationSection
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected ConfigurationSection getMap(Map map, Field field, ConfigurationSection cs, String path, int depth) throws Exception {
        depth++;

        ConfigurationSection subCS = cs.createSection(path);
        Set<String> keys = map.keySet();

        if(keys.size() > 0) {
            for(String key : keys) {
                Object out = map.get(key);
                out = saveObject(out, field, cs, path + "." + key, depth);
                subCS.set(key, out);
            }
        }

        return subCS;
    }

    /**
     * This function saves a List into a ConfigurationSection
     *
     * @param list The list which should be saved
     * @param field The field from which the List comes from
     * @param cs The ConfigurationSection which should be used to save into
     * @param path The starting path which should be used for saving
     * @param depth Recursive Typed Parameter
     * @return ConfigurationSection
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    protected ConfigurationSection getList(List list, Field field, ConfigurationSection cs, String path, int depth) throws Exception {
        depth++;

        ConfigurationSection subCS = cs.createSection(path);
        String key = path;

        if(key.lastIndexOf(".")>=0) {
            key = key.substring(key.lastIndexOf("."));
        }

        if(list != null && list.size() > 0) {
            for(int i = 0; i < list.size(); i++) {
                Object out = list.get(i);
                out = saveObject(out, field, cs, path+"."+key+(i+1), depth);
                subCS.set(key+(i+1), out);
            }
        }

        return subCS;
    }

    /**
     * Convert the Enum to a String
     *
     * @param enumObj The enum Object
     * @return String
     */
    @SuppressWarnings("rawtypes")
    protected String getEnum(Enum enumObj) {
        return enumObj.toString();
    }

    /**
     * Check if Field can be used. The field should not contain any of this modifiers: private, final, static, transient
     *
     * @param field The field to be checked
     * @return boolean
     */
    protected boolean doSkip(Field field) {
        return Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()) || Modifier.isPrivate(field.getModifiers());
    }
}