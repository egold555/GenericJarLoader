package org.golde.genericjarloader.loader;

import java.io.File;

public interface ILoaderImplementation {

    void onPreInitialize();

    void onInitialized();

    void onTerminate();

    File getPluginsFolder();
}
