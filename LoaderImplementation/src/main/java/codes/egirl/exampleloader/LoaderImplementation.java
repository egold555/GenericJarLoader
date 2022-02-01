package codes.egirl.exampleloader;

import org.golde.genericjarloader.loader.ILoaderImplementation;

import java.io.File;

public class LoaderImplementation implements ILoaderImplementation {

    @Override
    public void onPreInitialize() {
        //This is called before plugins are loaded.
    }

    @Override
    public void onInitialized() {
        //This is called when the plugins have completed loading.
    }

    @Override
    public void onTerminate() {
        //This is called when the plugins have been unloaded!
    }

    @Override
    public File getPluginsFolder() {
        return new File("test/plugins");
    }
}
