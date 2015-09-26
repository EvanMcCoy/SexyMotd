package com.qwertyness.sexymotd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MaintenanceConfig {
	File file = null;
	FileConfiguration config = null;
	Main plugin;
	
	public MaintenanceConfig(Main plugin) {
		this.plugin = plugin;
		this.reload();
		this.save();
	}
	
	public MaintenanceConfig(Main plugin, File file) {
		this(plugin);
		this.file = file;
	}
	
	@SuppressWarnings("deprecation")
	public void reload() {
	    if (file == null) {
	    	file = new File(plugin.getDataFolder(), "maintenance.yml");
	    }
	    config = YamlConfiguration.loadConfiguration(file);
	 
	    InputStream defConfigStream = plugin.getResource("maintenance.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        config.setDefaults(defConfig);
	    }
	}
	public FileConfiguration get() {
	    if (config == null) {
	        this.reload();
	    }
	    return config;
	}
	public void save() {
	    if (config == null || file == null) {
	    return;
	    }
	    try {
	        get().save(file);
	    } catch (IOException ex) {
	        plugin.getLogger().log(Level.SEVERE, "Could not save config to " + file, ex);
	    }
	}
}
