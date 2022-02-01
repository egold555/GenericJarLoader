package org.golde.genericjarloader.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.golde.genericjarloader.api.AbstractPlugin;
import org.golde.genericjarloader.api.PluginMetaData;
import org.golde.genericjarloader.api.LoadException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public class PluginLoader {

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();
    private final HashMap<File, AbstractPlugin> pluginMap = new HashMap<>();

    /**
     * Try to load a plugin
     *
     * @param file path to jar file
     */
    public void load(File file) {

        try {

            //Ensure our friend here is a jar file.
            if (!file.getName().contains(".jar")) {
                throw new LoadException("Plugin (" + file.getAbsolutePath() + ") was not a jar file!");
            }

            //Make sure the plugin isn't loaded
            //if it is, throw an exception
            if (this.pluginMap.containsKey(file)) {
                throw new LoadException("Plugin (" + file.getAbsolutePath() + ") already loaded!");
            }

            //Gets the plugin.json file out of the jar file
            final PluginMetaData pluginDescriptionFile = getPluginDescriptionFile(file);

            //get the class loader
            final ClassLoader loader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()}, getClass().getClassLoader());

            //load the main class specified in the plugin.json file
            final Class<?> clazz = Class.forName(pluginDescriptionFile.getMain(), true, loader);

            //Get an instance of the main class that should be ran
            final Class<? extends AbstractPlugin> instanceClass = clazz.asSubclass(AbstractPlugin.class);

            //Get the constructor of the instance class
            final Constructor<? extends AbstractPlugin> instanceClassConstructor = instanceClass.getConstructor();

            //Create a new instance of it
            final AbstractPlugin plugin = instanceClassConstructor.newInstance();

            //set the description file, so we can read it from the plugin instance.
            plugin.setMetaData(pluginDescriptionFile);

            //add the plugin to the map of enabled plugins
            this.pluginMap.put(file, plugin);

            //let us know it loaded
            System.out.println("Loaded '" + plugin.getMetaData().getName() + " v" + plugin.getMetaData().getVersion() + "'.");

            //call onEnable
            plugin.onEnable();

            //return the instance
        } catch (MalformedURLException e) {
            throw new LoadException("Failed to convert the file path to a URL.", e);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            throw new LoadException("Failed to create a new instance of the plugin.", exception);
        }
    }

    /**
     * Unload a plugin
     */
    public void unload(File file) {

        //Make sure we can't unload the plugin twice
        if (!pluginMap.containsKey(file)) {
            throw new LoadException("Can't unload a Plugin that wasn't loaded in the first place.");
        }

        //get the instance
        final AbstractPlugin plugin = this.pluginMap.get(file);

        //call on disable
        plugin.onDisable();

        //remove the file from the map so it can be enabled again
        this.pluginMap.remove(file);

        //Let us know it was unloaded
        System.out.println("Unloaded '" + plugin.getMetaData().getName() + " v" + plugin.getMetaData().getVersion() + "'");

        System.gc();
    }

    /**
     * Get a plugins description file from the jar
     *
     * @param file the jar file
     * @return a object with everything in the plugin description
     */
    private PluginMetaData getPluginDescriptionFile(File file) {

        try {

            //Create a zip file
            final ZipFile zipFile = new ZipFile(file);

            //Be able to loop over every file
            //TODO: Only really need the top level tbh
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();

            //Break the loop when this isn't null
            PluginMetaData pluginJson = null;

            //Go over every file in the zip file until we find the plugin.json file
            //TODO: Really only need the top level entries tbh
            while (entries.hasMoreElements() && pluginJson == null) {
                final ZipEntry entry = entries.nextElement();

                if (!entry.isDirectory() && entry.getName().equals("plugin.json")) {
                    final InputStream stream = zipFile.getInputStream(entry);
                    try {
                        //try to initalize it to a object
                        pluginJson = GSON.fromJson(new InputStreamReader(stream), PluginMetaData.class);
                    } catch (JsonParseException jsonParseException) {
                        throw new LoadException("Failed to parse JSON:", jsonParseException);
                    }
                }
            }

            //if we don't find it, throw an excepiton
            if (pluginJson == null) {
                zipFile.close();
                throw new LoadException("Failed to find plugin.json in the root of the jar.");
            }

            //close the zip
            zipFile.close();

            //return the object
            return pluginJson;
        } catch (IOException e) {
            throw new LoadException("Failed to open the jar as a zip:", e);
        }


    }

}
