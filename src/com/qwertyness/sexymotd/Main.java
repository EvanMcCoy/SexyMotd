package com.qwertyness.sexymotd;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.comphenix.protocol.wrappers.WrappedServerPing.CompressedImage;
import com.qwertyness.sexymotd.variable.Banned;
import com.qwertyness.sexymotd.variable.Opped;
import com.qwertyness.sexymotd.variable.Whitelisted;
import com.qwertyness.sexymotdengine.ActivePlugin;
import com.qwertyness.sexymotdengine.ActivePlugin.SexyMotdPlugin;
import com.qwertyness.sexymotdengine.CommandHandler;
import com.qwertyness.sexymotdengine.response.AnimatedText;
import com.qwertyness.sexymotdengine.response.Info;
import com.qwertyness.sexymotdengine.response.Maintenance;
import com.qwertyness.sexymotdengine.response.PlayerMessage;
import com.qwertyness.sexymotdengine.util.MotdConstructor;
import com.qwertyness.sexymotdengine.util.MotdPackage;
import com.qwertyness.sexymotdengine.util.PerformanceReport;
import com.qwertyness.sexymotdengine.util.ScrollTextConstructor.Position;
import com.qwertyness.sexymotdengine.variable.CustomVariable;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Main extends JavaPlugin implements Listener, SexyMotdPlugin {
	public static String PREFIX_ERROR = ChatColor.DARK_RED + "[" + ChatColor.RED + "SexyMotd" + ChatColor.DARK_RED + "] " + ChatColor.GOLD;
	public static String PREFIX_NORMAL = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "SexyMotd" + ChatColor.DARK_GREEN + "] " + ChatColor.YELLOW;
	public static Main instance;
    public static File file;
    public static String version;
	public static Logger logger = Bukkit.getLogger();
	public MaintenanceConfig maintenanceConfig;

	public void onEnable() {
		instance = this;
        version = getDescription().getVersion();
        
		// Hook in ProtocolLib
		Plugin plugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");
		if (plugin == null) {
			logger.severe("ProtocolLib is NOT installed, SexyMotd is being disabled!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		// Register events
		Bukkit.getPluginManager().registerEvents(this, this);
		
		// Config
		saveDefaultConfig();
		saveResource("maintenance.yml", false);
		maintenanceConfig = new MaintenanceConfig(this);
		IpList.createConfig();
		
		
		// ProtocolLib
		setupListener();
		
		Info.variables.add(new Banned());
		Info.variables.add(new Whitelisted());
		Info.variables.add(new Opped());
		ActivePlugin.initialize(this);
	}
	
	public void onDisable() {
		ActivePlugin.disable();
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (!command.getName().equals("motd")) {
			return false;
		}
		List<String> messages = CommandHandler.onCommand(args);
		for (String message : messages) {
			sender.sendMessage(color(message));
		}
		return true;
	}

	public void setupListener() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, Arrays.asList(new PacketType[] { PacketType.Status.Server.OUT_SERVER_INFO }), new ListenerOptions[] { ListenerOptions.ASYNC }) {
			
			public void onPacketSending(PacketEvent e) {
				WrappedServerPing ping = e.getPacket().getServerPings().read(0);
				InetAddress address = e.getPlayer().getAddress().getAddress();
				
				MotdConstructor constructor = new MotdConstructor(address);
				constructor.preBuild();
				MotdPackage motdPackage = constructor.dynamicBuild(0);
				
				ping.setMotD(motdPackage.motd);
				
				Info info = (((Maintenance)Info.getMaintenance()).ENABLED) ? Info.getMaintenance(): Info.getInfo();
				
				if (info.ENABLE_PLAYER_MESSAGE) {
					List<WrappedGameProfile> players = new ArrayList<WrappedGameProfile>();
					for (String string : motdPackage.playerMessage) {
						players.add(new WrappedGameProfile("1", string));
					}
					ping.setPlayers(players);
				}
				if (info.ENABLE_PLAYER_COUNT) {
					ping.setPlayersOnline(motdPackage.players);
				}
				if (info.ENABLE_MAX_PLAYERS) {
					ping.setPlayersMaximum(motdPackage.maxPlayers);
				}
				if (info.ENABLE_FAKE_VERSION) {
					ping.setVersionProtocol(-1);
					ping.setVersionName(motdPackage.version);
				}
				if (info.ENABLE_AVATAR_ICON || info.ENABLE_OVERLAY_IMAGE) {
					try {
						ping.setFavicon(CompressedImage.fromPng(motdPackage.favicon));
					} catch (IOException e1) {}
				}
			}
			
		});
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		String ip = player.getAddress().getAddress().getHostAddress().replace('.', '-');
		IpList.config.set(ip, player.getName());
		IpList.save();
		if (Info.getMaintenance().ENABLED) {
			if (Info.getMaintenance().maintainers != null) {
				for (UUID maintainer : Info.getMaintenance().maintainers) {
					if (e.getPlayer().getUniqueId().compareTo(maintainer) == 0) {
						return;
					}
				}
			}
			e.getPlayer().kickPlayer(Info.getMaintenance().KICK_MESSAGE);
		}
	}

	public static Main getInstance() {
		return instance;
	}

	@Override
	public String serverName() {
		return this.getServer().getName();
	}

	@Override
	public int maxPlayers() {
		return this.getServer().getMaxPlayers();
	}

	@Override
	public String version() {
		String version = this.getServer().getBukkitVersion();
		return version.substring(version.lastIndexOf(' ')+1, version.length()-1);
	}

	@Override
	public boolean newPlayer(String ip) {
		return IpList.config.getString(ip.replace('.', '-')) == null;
	}

	@Override
	public String playerName(String ip) {
		return IpList.config.getString(ip.replace('.', '-'));
	}

	@Override
	public String[] groupNames(String playerName) {
		if (Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx") != null) {
			return PermissionsEx.getPermissionManager().getUser(playerName).getParentIdentifiers().toArray(new String[0]);
		}
		return new String[0];
	}

	@Override
	public int onlinePlayers() {
		return this.getServer().getOnlinePlayers().size();
	}

	@Override
	public String[] playerNames() {
		String[] playerNames = new String[onlinePlayers()];
		for (int i = 0;i < onlinePlayers();i++) {
			playerNames[i] = this.getServer().getOnlinePlayers().toArray(new Player[0])[i].getDisplayName();
		}
		return playerNames;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public UUID getPlayerUUID(String name) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(name);
		if (player != null) {
			return player.getUniqueId();
		}
		return null;
	}

	@Override
	public String color(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}

	@Override
	public void loadConfig(Info info) {
		Configuration config;
		if (info instanceof Maintenance) {
			this.maintenanceConfig.reload();
			config = this.maintenanceConfig.get();
    		Maintenance maintenance = (Maintenance) info;
    		maintenance.ENABLED = config.getBoolean("enabled");
    		List<String> stringList = config.getStringList("maintainers");
    		maintenance.maintainers = new ArrayList<UUID>();
    		for (String string : stringList) {
    			maintenance.maintainers.add(UUID.fromString(string));
    		}
    		maintenance.KICK_MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("kickMessage"));
    	}
		else {
			this.reloadConfig();
			config = this.getConfig();
		}
		
		info.performanceLogging = false;
    	info.pingLogging = config.getBoolean("pingLogging");
    	long time = new Date().getTime();
    	
    	for (String variable : config.getConfigurationSection("variables").getKeys(false)) {
    		ConfigurationSection variableSection = config.getConfigurationSection("variables." + variable);
    		info.customVariables.add(new CustomVariable(variable, variableSection.getString("variable"), variableSection.getString("operator"), variableSection.getString("condition"), 
    				variableSection.getString("value"), variableSection.getString("negValue")));
    	}
    	
    	info.MOTDS = new ArrayList<AnimatedText>();
    	for (String motd : config.getStringList("motds")) {
    		info.MOTDS.add(new AnimatedText(Arrays.asList(motd), Position.MOTD, false, info));
    	}
    	
    	info.ENABLE_MAX_PLAYERS = config.getBoolean("maxPlayers.enabled");
    	if (config.getString("maxPlayers.maxPlayers") != null) {
    		info.STRING_MAX_PLAYERS = config.getString("maxPlayers.maxPlayers");
    	}
    	if (config.getIntegerList("maxPlayers.maxPlayers") != null) {
    		info.MAX_PLAYERS = config.getInt("maxPlayers.maxPlayers");
    	}
    	
    	info.ENABLE_PLAYER_COUNT = config.getBoolean("playerCount.enabled");
    	if (config.getStringList("playerCount.playerCount") != null) {
    		info.STRING_PLAYER_COUNT = config.getStringList("playerCount.playerCount");
    	}
    	if (config.getIntegerList("playerCount.playerCount") != null) {
    		info.PLAYER_COUNT = config.getIntegerList("playerCount.playerCount");
    	}
    	
    	info.ENABLE_AVATAR_ICON = config.getBoolean("icon.enableAvatarIcon");
    	info.ENABLE_OVERLAY_IMAGE = config.getBoolean("icon.enableOverlayImage");
    	info.IMAGE_PATH = config.getString("icon.imagePath");
    	
    	info.ENABLE_PLAYER_MESSAGE = config.getBoolean("playerMessage.enabled");
    	List<AnimatedText> pmList = new ArrayList<AnimatedText>();
    	for (String playerMessage : config.getStringList("playerMessage.playerMessage")) {
    		pmList.add(new AnimatedText(Arrays.asList(playerMessage), Position.PLAYER_MESSAGE, false, info));
    	}
    	info.PLAYER_MESSAGE = new PlayerMessage(pmList, info);
    	
    	info.ENABLE_FAKE_VERSION = config.getBoolean("fakeVersion.enabled");
		info.FAKE_VERSION = new AnimatedText(Arrays.asList(config.getString("fakeVersion.fakeVersion")), Position.VERSION, false, info);
    	
    	info.DEFAULT_PLAYER_NAME = config.getString("defaultPlayerName");
    	
    	if (info.performanceLogging) {
    		PerformanceReport.configRead = new Date().getTime() - time;
    	}
    	
    	
	}
	
	public void setConfigValue(Info info, String key, Object value) {
		Configuration config = null;
		if (info instanceof Maintenance) {
			config = this.maintenanceConfig.get();
		}
		else {
			config = this.getConfig();
		}
		config.set(key, value);
	}
	
	public void saveConfig(Info info) {
		if (info instanceof Maintenance) {
			this.maintenanceConfig.save();
		}
		else {
			this.saveConfig();
		}
	}
}