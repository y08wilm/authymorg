/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.Plugin
 */
package io.github.y08wilm.authy.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigurationManager {
	static ConfigurationManager instance = new ConfigurationManager();
	FileConfiguration data;
	File dfile;
	public List<Plugin> plugins = new ArrayList<Plugin>();

	public static ConfigurationManager getInstance() {
		return instance;
	}

	public void setup(File file) {
		this.dfile = file;
		if (!this.dfile.exists()) {
			try {
				this.dfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.data = YamlConfiguration.loadConfiguration((File) this.dfile);
	}

	public FileConfiguration getData() {
		return this.data;
	}

	public void clearData() {
		try {
			this.dfile.delete();
			this.setup(this.dfile);
		} catch (Exception exception) {
			// empty catch block
		}
	}

	public void saveData() {
		try {
			this.data.save(this.dfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reloadData() {
		this.data = YamlConfiguration.loadConfiguration((File) this.dfile);
	}
}
