package net.pixelizedmc.sexymotd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CM {

    final static String path = "plugins/SexyMotd/config.yml";
    static File file = new File(path);
    static FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    public static boolean ENABLED;
    public static boolean ENABLE_MOTD;
    public static String MOTD;
    public static String RAWMOTD;
    public static boolean ENABLE_FAKE_MAX_PLAYERS;
    public static int FAKE_MAX_PLAYERS;
    public static boolean ENABLE_FAKE_PLAYERS;
    public static int FAKE_PLAYERS;
    public static String DEFAULT_PLAYERNAME;
    public static boolean ENABLE_AVATAR_ICON;
    public static boolean ENABLE_OVERLAY_IMAGE;
    public static boolean OVERLAY_IMAGE_URL;
    public static String OVERLAY_IMAGE_PATH;
    public static boolean ENABLE_PLAYER_MESSAGE;
    public static List<String> PLAYER_MESSAGE;
    public static List<String> RAW_PLAYER_MESSAGE;
    public static boolean CHECK_UPDATES;
    public static boolean ENABLE_FAKE_VERSION;
    public static String FAKE_VERSION;
    
    public static void createConfig() {
        config.addDefault("Enabled", true);
        config.addDefault("CheckUpdates", true);
        config.addDefault("Motd.Enabled", true);
        config.addDefault("Motd.Motd", "&6Hello, &e&l%playername%&6!%newline%&6Welcome to &4&l%servername%&6!");
        config.addDefault("FakePlayers.Enabled", true);
        config.addDefault("FakePlayers.Players", -125);
        config.addDefault("FakeMaxPlayers.Enabled", true);
        config.addDefault("FakeMaxPlayers.Players", -125987);
        config.addDefault("IPLogging.DefaultName", "Guest");
        config.addDefault("AvatarIcon.Enabled", true);
        config.addDefault("AvatarIcon.OverlayImage.Enabled", true);
        config.addDefault("AvatarIcon.OverlayImage.Url", false);
        config.addDefault("AvatarIcon.OverlayImage.Path", "plugins/SexyMotd/SexyImage.png");
        config.addDefault("PlayerMessage.Enabled", true);
        config.addDefault("FakeVersion.Enabled", false);
        config.addDefault("FakeVersion.Version", "&eMaintenance mode");
        
        List<String> msg = new ArrayList<>();
        msg.add("&b=============== &e%servername%&b ===============");
        msg.add("&4&lWelcome, &e&l%playername%&4&l!");
        msg.add("&b============================================");
        msg.add("&rWebsite: &ahttp://pixelizedmc.net");
        msg.add("&rIP: &aplay.pixelizedmc.net");
        msg.add("&rOnline: &a%online_players%/%max_players%");
        msg.add("&rVersion: &a%version%");
        config.addDefault("PlayerMessage.Message", msg);
        
        config.options().copyDefaults(true);
        save();
    }
    
	public static void readConfig() {
		ENABLED = config.getBoolean("Enabled");
        CHECK_UPDATES = config.getBoolean("CheckUpdates");
        ENABLE_MOTD = config.getBoolean("Motd.Enabled");
        MOTD = parseMotd(config.getString("Motd.Motd"));
        ENABLE_FAKE_PLAYERS = config.getBoolean("FakePlayers.Enabled");
        FAKE_PLAYERS = config.getInt("FakePlayers.Players");
        ENABLE_FAKE_MAX_PLAYERS = config.getBoolean("FakeMaxPlayers.Enabled");
        FAKE_MAX_PLAYERS = config.getInt("FakeMaxPlayers.Players");
        DEFAULT_PLAYERNAME = config.getString("IPLogging.DefaultName");
        ENABLE_AVATAR_ICON = config.getBoolean("AvatarIcon.Enabled");
        ENABLE_OVERLAY_IMAGE = config.getBoolean("AvatarIcon.OverlayImage.Enabled");
        OVERLAY_IMAGE_URL = config.getBoolean("AvatarIcon.OverlayImage.Url");
        OVERLAY_IMAGE_PATH = config.getString("AvatarIcon.OverlayImage.Path");
        ENABLE_PLAYER_MESSAGE = config.getBoolean("PlayerMessage.Enabled");
        RAW_PLAYER_MESSAGE = config.getStringList("PlayerMessage.Message");
        PLAYER_MESSAGE = new ArrayList<>();
        ENABLE_FAKE_VERSION = config.getBoolean("FakeVersion.Enabled");
        FAKE_VERSION = parseMotd(config.getString("FakeVersion.Version"));
        for (String s:config.getStringList("PlayerMessage.Message")) {
        	PLAYER_MESSAGE.add(parseMotd(s));
        }
    }

    public static void save() {
        try {
            config.save(path);
        } catch (IOException e) {
        	Main.logger.severe("[SexyMotd] Error 'createConfig' on " + path);
        }
    }
    
	public static String parseMotd(String motd) {
		String output = ChatColor.translateAlternateColorCodes('&', motd);
		RAWMOTD = output;
		output = output.replaceFirst("%newline%", "\n");
		output = output.replaceAll("%servername%", Bukkit.getServerName());
		String ver = Bukkit.getServer().getVersion();
		ver = ver.split("\\(")[1];
		ver = ver.substring(4, ver.length() - 1);
		output = output.replaceAll("%version%", ver);
		return output;
    }
}
