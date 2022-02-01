package org.golde.genericjarloader.loader;

import lombok.Getter;
import org.golde.genericjarloader.api.LoadException;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class BootStrap {

    @Getter private static BootStrap instance;
    private final PluginLoader loader = new PluginLoader();


    public void create(final ILoaderImplementation impl) {
        instance = this;
        final File folder = impl.getPluginsFolder();

        if (folder == null) {
            throw new LoadException("Failed to load plugins. (Folder was null)");
        }

        impl.onPreInitialize();
        Arrays.stream(Objects.requireNonNull(folder.listFiles())).forEachOrdered(this.loader::load);

        //Sleep for 3 seconds!!! uwu!
        try {
            Thread.sleep(3000);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        impl.onInitialized();
    }

    public void close(final ILoaderImplementation impl) {
        Arrays.stream(Objects.requireNonNull(impl.getPluginsFolder().listFiles())).forEachOrdered(this.loader::unload);
        impl.onTerminate();
    }

    public static void initialize(final ILoaderImplementation impl) {
        new BootStrap().create(impl);
    }

    public static void destroy(final ILoaderImplementation impl) {
        BootStrap.getInstance().close(impl);
    }
}
