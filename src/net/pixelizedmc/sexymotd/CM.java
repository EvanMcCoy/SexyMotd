package net.pixelizedmc.sexymotd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CM {
	private static Main plugin;
    final static String path = "plugins/SexyMotd/config.yml";
    static File file = new File(path);
    static FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    public static boolean ENABLED;
    public static boolean ENABLE_MOTD;
    public static List<String> MOTDS;
    public static List<String> RAWMOTDS = new ArrayList<String>();
    public static boolean ENABLE_FAKE_MAX_PLAYERS;
    public static int FAKE_MAX_PLAYERS;
    public static boolean ENABLE_FAKE_PLAYERS;
    public static List<Integer> FAKE_PLAYERS;
    public static String DEFAULT_PLAYERNAME;
    public static boolean ENABLE_AVATAR_ICON;
    public static boolean ENABLE_OVERLAY_IMAGE;
    public static String OVERLAY_IMAGE_PATH;
    public static boolean ENABLE_PLAYER_MESSAGE;
    public static List<String> PLAYER_MESSAGE;
    public static List<String> RAW_PLAYER_MESSAGE;
    public static boolean CHECK_UPDATES;
    public static boolean ENABLE_FAKE_VERSION;
    public static String FAKE_VERSION;
    public static boolean ENABLE_PROTOCOL_OVERRIDE;
    
    public static void createConfig() {
        config.addDefault("Enabled", true);
        config.addDefault("CheckUpdates", true);
        config.addDefault("Motd.Enabled", true);
        config.addDefault("Motd.Motd", Arrays.asList("&6Hello, &e&l%playername%&6!%newline%&6Welcome to &4&l%servername%&6!", "A random MOTD!"));
        config.addDefault("FakePlayers.Enabled", true);
        config.addDefault("FakePlayers.Players", Arrays.asList(-125, 4, 100));
        config.addDefault("FakeMaxPlayers.Enabled", true);
        config.addDefault("FakeMaxPlayers.Players", -125987);
        config.addDefault("IPLogging.DefaultName", "Guest");
        config.addDefault("AvatarIcon.Enabled", true);
        config.addDefault("AvatarIcon.OverlayImage.Enabled", true);
        config.addDefault("AvatarIcon.OverlayImage.Path", "plugins/SexyMotd/SexyImage.png");
        config.addDefault("PlayerMessage.Enabled", true);
        config.addDefault("FakeVersion.Enabled", false);
        config.addDefault("FakeVersion.Version", "&eMaintenance mode!");
        config.addDefault("FakeVersion.EnableProtocolOverride", true);
        
        List<String> msg = new ArrayList<>();
        msg.add("&b=============== &e%servername%&b ===============");
        msg.add("&4&lWelcome, &e&l%playername%&4&l!");
        msg.add("&b============================================");
        msg.add("&rWebsite: &ahttp://pixelizedmc.net");
        msg.add("&rIP: &aplay.pixelizedmc.net");
        msg.add("&rOnline: &a%online_players%/%max_players%");
        msg.add("&rVersion: &a%version%");
        config.addDefault("PlayerMessage.Message", msg);
        
        plugin = Main.instance;
        config.options().copyDefaults(true);
        save();
    }
    
	public static void readConfig() {
		try {
			ENABLED = config.getBoolean("Enabled");
	        CHECK_UPDATES = config.getBoolean("CheckUpdates");
	        ENABLE_MOTD = config.getBoolean("Motd.Enabled");
	        MOTDS = parseMotd(config.getStringList("Motd.Motd"));
	        ENABLE_FAKE_PLAYERS = config.getBoolean("FakePlayers.Enabled");
	        FAKE_PLAYERS = config.getIntegerList("FakePlayers.Players");
	        ENABLE_FAKE_MAX_PLAYERS = config.getBoolean("FakeMaxPlayers.Enabled");
	        FAKE_MAX_PLAYERS = config.getInt("FakeMaxPlayers.Players");
	        DEFAULT_PLAYERNAME = config.getString("IPLogging.DefaultName");
	        ENABLE_AVATAR_ICON = config.getBoolean("AvatarIcon.Enabled");
	        ENABLE_OVERLAY_IMAGE = config.getBoolean("AvatarIcon.OverlayImage.Enabled");
	        OVERLAY_IMAGE_PATH = config.getString("AvatarIcon.OverlayImage.Path");
	        ENABLE_PLAYER_MESSAGE = config.getBoolean("PlayerMessage.Enabled");
	        RAW_PLAYER_MESSAGE = config.getStringList("PlayerMessage.Message");
	        PLAYER_MESSAGE = new ArrayList<>();
	        ENABLE_FAKE_VERSION = config.getBoolean("FakeVersion.Enabled");
	        FAKE_VERSION = parseMotd(Arrays.asList(config.getString("FakeVersion.Version"))).get(0);
	        ENABLE_PROTOCOL_OVERRIDE = config.getBoolean("FakeVersion.EnableProtocolOverride");
	        PLAYER_MESSAGE = parseMotd(config.getStringList("PlayerMessage.Message"));
		} catch (NullPointerException e) {
			File file = new File(plugin.getDataFolder() + File.separator + "config.yml");
			file.delete();
			file = new File(path);
			config = YamlConfiguration.loadConfiguration(file);
			createConfig();
		}
    }

    public static void save() {
        try {
            config.save(path);
        } catch (IOException e) {
        	Main.logger.severe("[SexyMotd] Error 'createConfig' on " + path);
        }
    }
    
    public static void reload() {
    	plugin.reloadConfig();
    	config = plugin.getConfig();
    	readConfig();
    }
    
	public static List<String> parseMotd(List<String> motd) {
		List<String> output = new ArrayList<String>();
		for (String string : motd) {
			RAWMOTDS.add(string);
			string = ChatColor.translateAlternateColorCodes('&', string);
			string = string.replaceFirst("%newline%", "\n");
			string = string.replaceAll("%servername%", Bukkit.getServerName());
			String ver = Bukkit.getServer().getVersion();
			ver = ver.split("\\(")[1];
			ver = ver.substring(4, ver.length() - 1);
			string = string.replaceAll("%version%", ver);
			output.add(string);
		}
		return output;
    }
}
