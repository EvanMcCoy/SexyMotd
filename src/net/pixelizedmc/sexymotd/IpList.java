package net.pixelizedmc.sexymotd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class IpList {
	private static Main plugin;
	final static String path = "plugins/SexyMotd/IpList.yml";
    static File file = new File(path);
    public static FileConfiguration config = YamlConfiguration.loadConfiguration(file);
    
    public static void createConfig() {
    	plugin = Main.instance;
        config.options().copyDefaults(true);
        save();
    }
    
    public static void save() {
        try {
            config.save(path);
        } catch (IOException e) {
            System.out.println("[SexyMotd] Error 'createConfig' on " + path);
        }
    }
    public static void reload() {
    	if (file == null) {
	    	file = new File(path);
	    }
	    config = YamlConfiguration.loadConfiguration(file);
	 
	    InputStream defConfigStream = plugin.getResource("IpList.yml");
	    if (defConfigStream != null) {
	        @SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        config.setDefaults(defConfig);
	    }
    }
}
