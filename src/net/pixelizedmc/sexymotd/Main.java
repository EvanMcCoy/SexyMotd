package net.pixelizedmc.sexymotd;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;

public class Main extends JavaPlugin implements Listener {
	public static String PREFIX_ERROR = ChatColor.DARK_RED + "[" + ChatColor.RED + "SexyMotd" + ChatColor.DARK_RED + "] " + ChatColor.GOLD;
	public static String PREFIX_NORMAL = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "SexyMotd" + ChatColor.DARK_GREEN + "] " + ChatColor.YELLOW;
	public static Main instance;
    public static File file;
    public static String version;
    public static String updater_name;
    public static String updater_version;
    public static String updater_link;
    public static boolean updater_available;
	public static Logger logger = Bukkit.getLogger();

	public void onEnable() {
		instance = this;
        version = getDescription().getVersion();
        file = getFile();
        
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
		CM.createConfig();
		CM.readConfig();
		IpList.createConfig();
		
		// Register commands
		Bukkit.getPluginCommand("motd").setExecutor(new Commands());
		
		// hook in metrics
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			logger.warning("SexyMotd couldn't connect to Metrics :(");
		}
		
		// Extract image
		if (CM.ENABLE_OVERLAY_IMAGE) {
			if (!CM.OVERLAY_IMAGE_PATH.contains("http://")) {
				File img = new File(CM.OVERLAY_IMAGE_PATH);
				if (!img.exists()) {
					CM.OVERLAY_IMAGE_PATH = "plugins/SexyMotd/SexyImage.png";
					CM.config.set("AvatarIcon.OverlayImage.Path", "plugins/SexyMotd/SexyImage.png");
					CM.save();
					saveResource("SexyImage.png", true);
				}
			}
		}
		
		// Check updates
		if (CM.CHECK_UPDATES) {
			checkUpdate();
		}
		
		// ProtocolLib
		setupListener();
	}

	public void setupListener() {
		if (CM.ENABLED) {
			ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, Arrays.asList(new PacketType[] { PacketType.Status.Server.OUT_SERVER_INFO }), new ListenerOptions[] { ListenerOptions.ASYNC }) {
				
				public void onPacketSending(PacketEvent e) {
					WrappedServerPing ping = e.getPacket().getServerPings().read(0);
					InetAddress address = e.getPlayer().getAddress().getAddress();
					if (CM.ENABLE_MOTD) {
						ping.setMotD(Utils.generateMotd(CM.MOTDS.get(new Random().nextInt(CM.MOTDS.size())), address));
					}
					if (CM.ENABLE_PLAYER_MESSAGE) {
						ping.setPlayers(Utils.getMessage(CM.PLAYER_MESSAGE, address));
					}
					if (CM.ENABLE_FAKE_PLAYERS) {
						ping.setPlayersOnline(CM.FAKE_PLAYERS.get(new Random().nextInt(CM.FAKE_PLAYERS.size())));
					}
					if (CM.ENABLE_FAKE_MAX_PLAYERS) {
						ping.setPlayersMaximum(CM.FAKE_MAX_PLAYERS);
					}
					if (CM.ENABLE_FAKE_VERSION) {
						ping.setVersionName(CM.FAKE_VERSION);
						if (CM.ENABLE_PROTOCOL_OVERRIDE) {
							ping.setVersionProtocol((byte)-1);
						}
					}
					
					
				}
				
			});
		}
	}

	@EventHandler
	public void onPing(ServerListPingEvent e) {
		if (CM.ENABLED) {
			if (CM.ENABLE_AVATAR_ICON) {
				e.setServerIcon(Utils.getIcon(e.getAddress()));
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		String ip = Utils.convertIp(player.getAddress().getAddress());
		String name = player.getName();
		IpList.config.set(ip, name);
		IpList.save();
        if (player.hasPermission("sexymotd.update.notify") && updater_available) {
        	Utils.sendMessage(player, "A new update (" + updater_name + ") is available!");
        	Utils.sendMessage(player, "Please type /bm update to update it automatically, or click the link below do download it manually:");
        	Utils.sendMessage(player, updater_link);
        }
	}

	public static Main getInstance() {
		return instance;
	}
	
    public static void checkUpdate() {
        Updater updater = new Updater(getInstance(), 76246, file, Updater.UpdateType.NO_DOWNLOAD, false);
        updater_available = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
        updater_name = updater.getLatestName();
        updater_version = updater.getLatestGameVersion();
        updater_link = updater.getLatestFileLink();
    }
}