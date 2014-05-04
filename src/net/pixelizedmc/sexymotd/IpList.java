package net.pixelizedmc.sexymotd;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class IpList {
	final static String path = "plugins/SexyMotd/IpList.yml";
    static File file = new File(path);
    public static FileConfiguration config = YamlConfiguration.loadConfiguration(file);
    
    public static void createConfig() {
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
}
