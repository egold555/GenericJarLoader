package org.golde.genericjarloader.api;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractPlugin {

    @Getter @Setter
    private PluginMetaData metaData;

    public abstract void onEnable();

    public abstract void onDisable();

}
