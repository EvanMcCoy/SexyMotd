package net.pixelizedmc.sexymotd;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import net.pixelizedmc.sexymotd.Variable.Operator;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.util.CachedServerIcon;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.comphenix.protocol.wrappers.WrappedGameProfile;

public class Utils {
	public static String convertIp(InetAddress address) {
		address.getHostAddress();
		List<Object> iplist = new ArrayList<>();
		for (Byte b:address.getAddress()) {
			iplist.add(b);
		}
		return StringUtils.replace(address.getHostAddress(), ".", "-");
	}
	
	public static boolean isInteger(String s) {
    	try {
        	Integer.parseInt(s); 
    	} catch(NumberFormatException e) { 
    		return false;
    	}
    	return true;
    }

	public static String generateMotd(String motd, InetAddress address) {
		return generateMotd(motd, convertIp(address));
	}
	
	public static String generateMotd(String motd, String address) {
		String output = motd;
		String playerName = IpList.config.getString(address);
		int onlinePlayers = Bukkit.getOnlinePlayers().length;
		int maxPlayers = Bukkit.getMaxPlayers();
		boolean newplayer = IpList.config.contains(address);
		boolean banned = Bukkit.getOfflinePlayer(UUID.fromString(IpList.config.getString(address))).isBanned();
		boolean whitelisted = Bukkit.getOfflinePlayer(UUID.fromString(IpList.config.getString(address))).isWhitelisted();
		List<String> groupNames = null;
		if (Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx") != null) {
			groupNames = PermissionsEx.getPermissionManager().getUser(IpList.config.getString(address)).getParentIdentifiers();
		}
		
		for (String variableName : CM.config.getConfigurationSection("variables").getKeys(false)) {
			Variable variable = new Variable(CM.config.getConfigurationSection("variables." + variableName));
			if (!motd.toLowerCase().contains(variable.name)) {
				continue;
			}
			if (variable.builtInVariable.contains("%playername%".toLowerCase())) {
				if (variable.operator == Operator.EQUAL) {
					if (playerName.equals(variable.condition)) {
						output = output.replaceAll("(?i)" + variable.name, variable.value);
					}
				}
			}
			else if (variable.builtInVariable.contains("%online_players%".toLowerCase())) {
				if (checkNumericCondition(variable, onlinePlayers)) {
					output = output.replaceAll("(?i)" + variable.name, variable.value);
				}
			}
			else if (variable.builtInVariable.contains("%max_players%".toLowerCase())) {
				if (checkNumericCondition(variable, maxPlayers)) {
					output = output.replaceAll("(?i)" + variable.name, variable.value);
				}
			}
			else if (variable.builtInVariable.contains("%newplayer%".toLowerCase())) {
				if (variable.operator == Operator.EQUAL) {
					if ((variable.condition.equalsIgnoreCase("true") && newplayer) || (variable.condition.equalsIgnoreCase("false") && !newplayer)) {
						output = output.replaceAll("(?i)" + variable.name, variable.value);
					}
				}
			}
			else if (variable.builtInVariable.contains("%banned%".toLowerCase())) {
				if (variable.operator == Operator.EQUAL) {
					if ((variable.condition.equalsIgnoreCase("true") && banned) || (variable.condition.equalsIgnoreCase("false") && !banned)) {
						output = output.replaceAll("(?i)" + variable.name, variable.value);
					}
				}
			}
			else if (variable.builtInVariable.contains("%whitelisted%".toLowerCase())) {
				if (variable.operator == Operator.EQUAL) {
					if ((variable.condition.equalsIgnoreCase("true") && whitelisted) || (variable.condition.equalsIgnoreCase("false") && !whitelisted)) {
						output = output.replaceAll("(?i)" + variable.name, variable.value);
					}
				}
			}
			else if (variable.builtInVariable.contains("%groupname%".toLowerCase())) {
				if (groupNames == null) {
					continue;
				}
				if (groupNames.contains(variable.condition)) {
					output = output.replaceAll("(?i)" + variable.name, variable.value);
				}
			}
		}
		
		if (motd.toLowerCase().contains("%playername%".toLowerCase())) {
			if (IpList.config.contains(address)) {
				output = output.replaceAll("(?i)%playername%", playerName);
			} else {
				output = output.replaceAll("(?i)%playername%", CM.DEFAULT_PLAYERNAME);
			}
		}
		if (motd.toLowerCase().contains("%online_players%".toLowerCase())) {
			output = output.replaceAll("(?i)%online_players%", Integer.toString(onlinePlayers));
		}
		if (motd.toLowerCase().contains("%max_players%".toLowerCase())) {
			output = output.replaceAll("(?i)%max_players%", Integer.toString(maxPlayers));
		}
		if (motd.toLowerCase().contains("%groupname%".toLowerCase())) {
			output = output.replaceAll("(?i)%groupname%", groupNames.get(0));
		}
		return output;
	}
	
	public static void sendError(CommandSender p, String msg) {
		p.sendMessage(Main.PREFIX_ERROR + msg);
	}
	public static void sendMessage(CommandSender p, String msg) {
		p.sendMessage(Main.PREFIX_NORMAL + msg);
	}
	
	public static List<WrappedGameProfile> getMessage(List<String> msgs, InetAddress address) {
		List<WrappedGameProfile> output = new ArrayList<WrappedGameProfile>();
		for (String msg:msgs) {
			output.add(new WrappedGameProfile("1", generateMotd(msg, convertIp(address))));
		}
		return output;
	}
	
	public static CachedServerIcon getIcon(InetAddress address) {
		String ip = convertIp(address);
		if (IpList.config.contains(ip)) {
			String player = IpList.config.getString(ip);
			BufferedImage icon = null;
			try {
				icon = ImageIO.read(new URL("https://minotar.net/helm/" + player + "/64.png"));
			} catch (IOException e) {
				
			}
			if (CM.ENABLE_OVERLAY_IMAGE) {
				File image = new File(CM.OVERLAY_IMAGE_PATH);
				if (image.exists()) {
					Image overlay = null;
					if (CM.OVERLAY_IMAGE_PATH.contains("http://")) {
						try {
							overlay = ImageIO.read(new URL(CM.OVERLAY_IMAGE_PATH));
						} catch (IOException e) {
							Main.logger.severe("FAILED TO GET OVERLAY IMAGE!");
							return Bukkit.getServerIcon();
						}
					} else {
						try {
							overlay = ImageIO.read(new File(CM.OVERLAY_IMAGE_PATH));
						} catch (IOException e) {
							Main.logger.severe("FAILED TO GET OVERLAY IMAGE!");
							return Bukkit.getServerIcon();
						}
					}
					if (overlay.getWidth(null) == 64 && overlay.getHeight(null) == 64) {
						icon.getGraphics().drawImage(overlay, 0, 0, null);
					} else {
						Main.logger.severe("THE OVERLAY IMAGE MUST BE 64x64!");
						return Bukkit.getServerIcon();
					}
				} else {
					Main.logger.severe("THE PATH FILE DOSENT EXIST!!!");
				}
			}
			try {
				return Bukkit.loadServerIcon(icon);
			} catch (Exception e) {
				return Bukkit.getServerIcon();
			}
		} else {
			return Bukkit.getServerIcon();
		}
	}
	
	public static boolean checkNumericCondition(Variable variable, int condition) {
		if (variable.operator == Operator.EQUAL) {
			if (Integer.parseInt(variable.condition) == condition) {
				return true;
			}
		}
		else if (variable.operator == Operator.GREATER_THAN) {
			if (condition > Integer.parseInt(variable.condition)) {
				return true;
			}
		}
		else if (variable.operator == Operator.LESS_THAN) {
			if (condition < Integer.parseInt(variable.condition)) {
				return true;
			}
		}
		return false;
	}
}
