package org.golde.genericjarloader.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PluginMetaData {
	private final String main;
	private final String name;
	private final String version;
}
