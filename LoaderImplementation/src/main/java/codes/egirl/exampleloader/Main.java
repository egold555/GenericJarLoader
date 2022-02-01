package codes.egirl.exampleloader;

import org.golde.genericjarloader.loader.BootStrap;
import org.golde.genericjarloader.loader.ILoaderImplementation;

public class Main {

    public static void main(String[] args) {
        final ILoaderImplementation implementation = new LoaderImplementation();

        BootStrap.initialize(implementation);

        BootStrap.destroy(implementation);
    }
}
