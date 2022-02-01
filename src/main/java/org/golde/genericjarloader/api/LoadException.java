package org.golde.genericjarloader.api;

/**
 * Generic exception for when shit hits the fan loading and unloading plugins
 *
 * @author Eric Golde
 */
public class LoadException extends RuntimeException {

    public LoadException(String whatHappened) {
        super(whatHappened);
    }

    public LoadException(String whatHappened, Throwable cause) {
        super(whatHappened, cause);
    }

}
