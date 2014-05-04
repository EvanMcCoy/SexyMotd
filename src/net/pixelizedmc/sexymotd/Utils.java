package net.pixelizedmc.sexymotd;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.util.CachedServerIcon;

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
		if (motd.toLowerCase().contains("%playername%".toLowerCase())) {
			if (IpList.config.contains(address)) {
				output = output.replaceAll("(?i)%playername%", IpList.config.getString(address));
			} else {
				output = output.replaceAll("(?i)%playername%", CM.DEFAULT_PLAYERNAME);
			}
		}
		if (motd.toLowerCase().contains("%online_players%".toLowerCase())) {
			output = output.replaceAll("(?i)%online_players%", Integer.toString(Bukkit.getOnlinePlayers().length));
		}
		if (motd.toLowerCase().contains("%max_players%".toLowerCase())) {
			output = output.replaceAll("(?i)%max_players%", Integer.toString(Bukkit.getMaxPlayers()));
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
				icon = ImageIO.read(new URL("https://minotar.net/avatar/" + player + "/64.png"));
			} catch (IOException e) {
				
			}
			if (CM.ENABLE_OVERLAY_IMAGE) {
				File image = new File(CM.OVERLAY_IMAGE_PATH);
				if (image.exists()) {
					Image overlay = null;
					if (CM.OVERLAY_IMAGE_URL) {
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
}
