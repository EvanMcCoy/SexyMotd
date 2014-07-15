package net.pixelizedmc.sexymotd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class Commands implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command c, String cmd, String[] args) {
		if (c.getName().equalsIgnoreCase("motd")) {
			if (args.length == 0) {
				printHelp(sender);
			} else {
				if (args[0].equalsIgnoreCase("addmotd")) {
					if (!sender.hasPermission("sexymotd.motd.set")) {
						Utils.sendError(sender, "You don't have permission!");
						return true;
					}
					if (args.length > 1) {
						List<String> listmotd = new ArrayList<>();
						for (int i = 1;i < args.length;i++) {
							listmotd.add(args[i]);
						}
						String motd = StringUtils.join(listmotd, ' ');
						CM.RAWMOTDS.add(motd);
						motd = ChatColor.translateAlternateColorCodes('&', motd);
						Utils.sendMessage(sender, "You have added the Motd: " + ChatColor.RESET + motd);
						motd = motd.replaceFirst("%newline%", "\n");
						motd = motd.replaceAll("%servername%", Bukkit.getServerName());
						String ver = Bukkit.getServer().getVersion();
						ver = ver.split("\\(")[1];
						ver = ver.substring(4, ver.length() - 1);
						motd = motd.replaceAll("%version%", ver);
						CM.MOTDS.add(motd);
						CM.config.set("Motd", CM.MOTDS);
						CM.save();
					} else {
						Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd addmotd <motd>");
					}
				} else if (args[0].equalsIgnoreCase("removemotd")) {
					if (!sender.hasPermission("sexymotd.motd.set")) {
						Utils.sendError(sender, "You don't have permission!");
						return true;
					}
					if (args.length < 2) {
						Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd removemotd <index>");
						return true;
					}
					int index = Integer.parseInt(args[1]);
					Utils.sendMessage(sender, "You have removed the Motd: " + ChatColor.RESET + CM.MOTDS.get(index));
					CM.RAWMOTDS.remove(index);
					CM.MOTDS.remove(index);
					CM.config.set("Motd", CM.MOTDS);
					CM.save();
				} else if (args[0].equalsIgnoreCase("listmotds")) {
					if (!sender.hasPermission("sexymotd.motd.see")) {
						Utils.sendError(sender, "You don't have permission!");
						return true;
					}
					for (String motd : CM.MOTDS) {
						sender.sendMessage(ChatColor.GOLD + ((Integer)CM.MOTDS.indexOf(motd)).toString() + ": " + ChatColor.RESET + motd);
					}
				} else if (args[0].equalsIgnoreCase("togglemotd")) {
					if (!sender.hasPermission("sexymotd.motd.toggle")) {
						Utils.sendError(sender, "You don't have permission!");
						return true;
					}
					if (args.length > 1) {
						Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd togglemotd");
					} else {
						if (CM.ENABLE_MOTD) {
							CM.ENABLE_MOTD = false;
							CM.config.set("Motd.Enabled", false);
							CM.save();
							Utils.sendMessage(sender, ChatColor.YELLOW + "Motd is now " + ChatColor.RED + "DISABLED");
						} else {
							CM.ENABLE_MOTD = true;
							CM.config.set("Motd.Enabled", true);
							CM.save();
							Utils.sendMessage(sender, "Motd is now " + ChatColor.GREEN + "ENABLED");
						}
					}
				} else if (args[0].equalsIgnoreCase("toggle")) {
					if (!sender.hasPermission("sexymotd.toggle")) {
						Utils.sendError(sender, "You don't have permission!");
						return true;
					}
					if (args.length > 1) {
						Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd toggle");
					} else {
						if (CM.ENABLED) {
							CM.ENABLED = false;
							CM.config.set("Enabled", false);
							CM.save();
							Utils.sendMessage(sender, ChatColor.YELLOW + "SexyMotd is now " + ChatColor.RED + "DISABLED");
						} else {
							CM.ENABLED = true;
							CM.config.set("Enabled", true);
							CM.save();
							Utils.sendMessage(sender, "SexyMotd is now " + ChatColor.GREEN + "ENABLED");
						}
					}
				} else if (args[0].equalsIgnoreCase("players")) {
					if (args.length == 1) {
						this.sendPlayerCommandUsage(sender);
					} else {
						if (args[1].equalsIgnoreCase("toggle")) {
							if (!sender.hasPermission("sexymotd.players.toggle")) {
								Utils.sendError(sender, "You don't have permission!");
								return true;
							}
							if (args.length == 2) {
								if (CM.ENABLE_FAKE_PLAYERS) {
									CM.ENABLE_FAKE_PLAYERS = false;
									CM.config.set("FakePlayers.Enabled", false);
									CM.save();
									Utils.sendMessage(sender, "FakePlayers are now " + ChatColor.RED + "DISABLED");
								} else {
									CM.ENABLE_FAKE_PLAYERS = true;
									CM.config.set("FakePlayers.Enabled", true);
									CM.save();
									Utils.sendMessage(sender, "FakePlayers are now " + ChatColor.GREEN + "ENABLED");
								}
							} else {
								this.sendPlayerCommandUsage(sender);
							}
						} else if (args[1].equalsIgnoreCase("add")) {
							if (!sender.hasPermission("sexymotd.players.set")) {
								Utils.sendError(sender, "You don't have permission!");
								return true;
							}
							if (args.length == 3) {
								if (Utils.isInteger(args[2])) {
									int players = Integer.parseInt(args[2]);
									CM.FAKE_PLAYERS.add(players);
									CM.config.set("FakePlayers.Players", CM.FAKE_PLAYERS);
									CM.save();
									Utils.sendMessage(sender, "Added to FakePlayers: " + ChatColor.DARK_GREEN + args[2]);
								} else {
									Utils.sendError(sender, args[2] + ChatColor.RED + " is not a number");
								}
							} else {
								this.sendPlayerCommandUsage(sender);
							}
						} else if (args[1].equalsIgnoreCase("remove")) {
							if (!sender.hasPermission("sexymotd.players.set")) {
								Utils.sendError(sender, "You don't have permission!");
								return true;
							}
							if (args.length == 3) {
								int index = Integer.parseInt(args[2]);
								Utils.sendMessage(sender, "Removed from FakePlayers: " + ChatColor.DARK_GREEN + CM.FAKE_PLAYERS.get(index));
								CM.FAKE_PLAYERS.remove(index);
								CM.save();
							}
							else {
								this.sendPlayerCommandUsage(sender);
							}
						} else if (args[1].equalsIgnoreCase("list")) {
							for (Integer players : CM.FAKE_PLAYERS) {
								sender.sendMessage(ChatColor.GOLD + ((Integer)CM.FAKE_PLAYERS.indexOf(players)).toString() + ": " + ChatColor.DARK_GREEN + players.toString());
							}
						} else {
							this.sendPlayerCommandUsage(sender);
						}
					}
				} else if (args[0].equalsIgnoreCase("maxplayers")) {
					if (args.length == 1) {
						Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd maxplayers toggle");
						Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd maxplayers set <number>");
					} else {
						if (args[1].equalsIgnoreCase("toggle")) {
							if (!sender.hasPermission("sexymotd.maxplayers.toggle")) {
								Utils.sendError(sender, "You don't have permission!");
								return true;
							}
							if (args.length == 2) {
								if (CM.ENABLE_FAKE_MAX_PLAYERS) {
									CM.ENABLE_FAKE_MAX_PLAYERS = false;
									CM.config.set("FakeMaxPlayers.Enabled", false);
									CM.save();
									Utils.sendMessage(sender, "FakeMaxPlayers are now " + ChatColor.RED + "DISABLED");
								} else {
									CM.ENABLE_FAKE_MAX_PLAYERS = true;
									CM.config.set("FakeMaxPlayers.Enabled", true);
									CM.save();
									Utils.sendMessage(sender, "FakeMaxPlayers are now " + ChatColor.GREEN + "ENABLED");
								}
							} else {
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd maxplayers toggle");
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd maxplayers set <number>");
							}
						} else if (args[1].equalsIgnoreCase("set")) {
							if (!sender.hasPermission("sexymotd.maxplayers.set")) {
								Utils.sendError(sender, "You don't have permission!");
								return true;
							}
							if (args.length == 3) {
								if (Utils.isInteger(args[2])) {
									int players = Integer.parseInt(args[2]);
									CM.FAKE_MAX_PLAYERS = players;
									CM.config.set("FakeMaxPlayers.Players", players);
									CM.save();
									Utils.sendMessage(sender, "FakeMaxPlayers is now set to: " + ChatColor.DARK_GREEN + args[2]);
								} else {
									Utils.sendError(sender, args[2] + ChatColor.RED + " is not a number");
								}
							} else {
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd maxplayers toggle");
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd maxplayers set <number>");
							}
						} else {
							Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd maxplayers toggle");
							Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd maxplayers set <number>");
						}
					}
				}
				//PLAYERMSG
				else if (args[0].equalsIgnoreCase("playermsg")) {
					if (args.length == 1) {
						Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg toggle");
						Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg add <line>");
						Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg remove <number>");
						Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg see");
					} else {
						if (args[1].equalsIgnoreCase("toggle")) {
							if (!sender.hasPermission("sexymotd.playermsg.toggle")) {
								Utils.sendError(sender, "You don't have permission!");
								return true;
							}
							if (args.length == 2) {
								if (CM.ENABLE_PLAYER_MESSAGE) {
									CM.ENABLE_PLAYER_MESSAGE = false;
									CM.config.set("PlayerMessage.Enabled", false);
									CM.save();
									Utils.sendMessage(sender, "PlayerMessage is now " + ChatColor.RED + "DISABLED");
								} else {
									CM.ENABLE_PLAYER_MESSAGE = true;
									CM.config.set("PlayerMessage.Enabled", true);
									CM.save();
									Utils.sendMessage(sender, "PlayerMessage is now " + ChatColor.GREEN + "ENABLED");
								}
							} else {
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg toggle");
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg add <line>");
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg remove <number>");
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg see");
							}
						} else if (args[1].equalsIgnoreCase("add")) {
							if (!sender.hasPermission("sexymotd.playermsg.add")) {
								Utils.sendError(sender, "You don't have permission!");
								return true;
							}
							if (args.length > 2) {
								List<String> m = new ArrayList<>();
								for (int i = 2;i < args.length;i++) {
									m.add(args[i]);
								}
								String msg = StringUtils.join(m, ' ');
								CM.RAW_PLAYER_MESSAGE.add(0, msg);
								CM.PLAYER_MESSAGE.add(0, CM.parseMotd(Arrays.asList(msg)).get(0));
								CM.config.set("PlayerMessage.Message", CM.RAW_PLAYER_MESSAGE);
								CM.save();
								Utils.sendMessage(sender, "Added line \"" + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', msg) + ChatColor.YELLOW + "\" to PlayerMessage");
							} else {
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg toggle");
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg add <line>");
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg remove <number>");
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg see");
							}
						} else if (args[1].equalsIgnoreCase("remove")) {
							if (!sender.hasPermission("sexymotd.playermsg.remove")) {
								Utils.sendError(sender, "You don't have permission!");
								return true;
							}
							if (args.length == 3) {
								if (Utils.isInteger(args[2])) {
									int num = Integer.parseInt(args[2]);
									if (CM.PLAYER_MESSAGE.size() >= num && num > 0) {
										CM.RAW_PLAYER_MESSAGE.remove(num - 1);
										CM.PLAYER_MESSAGE.remove(num - 1);
										CM.config.set("PlayerMessage.Message", CM.RAW_PLAYER_MESSAGE);
										CM.save();
										Utils.sendMessage(sender, "Removed line #" + args[2] + " in PlayerMessage");
									} else {
										Utils.sendError(sender, "Line #" + args[2] + " does not exist");
									}
								} else {
									Utils.sendError(sender, args[2] + ChatColor.RED + " is not a number");
								}
							} else {
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg toggle");
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg add <line>");
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg remove <number>");
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg see");
							}
						} else if (args[1].equalsIgnoreCase("see")) {
							if (!sender.hasPermission("sexymotd.playermsg.see")) {
								Utils.sendError(sender, "You don't have permission!");
								return true;
							}
							if (args.length == 2) {
								sender.sendMessage(ChatColor.YELLOW + "=== Player Message ===");
			    				int i = 0;
			    				for (String msg:CM.RAW_PLAYER_MESSAGE) {
			    					i++;
			    					sender.sendMessage(ChatColor.DARK_GREEN + Integer.toString(i) + ". " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', msg));
			    				}
							} else {
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg toggle");
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg add <line>");
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg remove <number>");
								Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg see");
							}
						} else {
							Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg toggle");
							Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg add <line>");
							Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg remove <number>");
							Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd playermsg see");
						}
					}
				} else if (args[0].equalsIgnoreCase("avataricon")) {
					if (!sender.hasPermission("sexymotd.avataricon")) {
						Utils.sendError(sender, "You don't have permission!");
						return true;
					}
					if (args.length == 1) {
						if (CM.ENABLE_AVATAR_ICON) {
							CM.ENABLE_AVATAR_ICON = false;
							CM.config.set("AvatarIcon.Enabled", false);
							CM.save();
							Utils.sendMessage(sender, "AvatarIcon is now " + ChatColor.RED + "DISABLED");
						} else {
							CM.ENABLE_AVATAR_ICON = true;
							CM.config.set("AvatarIcon.Enabled", true);
							CM.save();
							Utils.sendMessage(sender, "AvatarIcon is now " + ChatColor.GREEN + "ENABLED");
						}
					} else {
						Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd avataricon");
					}
				} else if (args[0].equalsIgnoreCase("help")) {
					printHelp(sender);
				} else if (args[0].equalsIgnoreCase("info")) {
					if (!sender.hasPermission("sexymotd.info")) {
						Utils.sendError(sender, "You don't have permission!");
						return true;
					}
					if (args.length == 1) {
				    	sender.sendMessage(ChatColor.DARK_AQUA + "===" + ChatColor.AQUA + " SexyMotd by the Pixelized Network " + ChatColor.DARK_AQUA + "===");
				    	sender.sendMessage(ChatColor.YELLOW + "Website: " + ChatColor.GREEN + "http://pixelizedmc.net");
				    	sender.sendMessage(ChatColor.YELLOW + "Official server: " + ChatColor.GREEN + "play.pixelizedmc.net");
				    	sender.sendMessage(ChatColor.YELLOW + "Author: " + ChatColor.GREEN + "Victor2748");
				    	sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.GREEN + Main.version);
				    	sender.sendMessage(ChatColor.YELLOW + "Sexiness: " + ChatColor.GREEN + "Level 80 ;)");
				    	sender.sendMessage(ChatColor.YELLOW + "BukkitDev page: " + ChatColor.GREEN + "http://dev.bukkit.org/bukkit-plugins/sexy-motd/");
				    	if (CM.ENABLED) {
					    	Utils.sendMessage(sender, "Enabled: " + ChatColor.GREEN + "true");
				    	} else {
				    		Utils.sendMessage(sender, "Enabled: " + ChatColor.RED + "false");
				    	}
				    	if (CM.ENABLE_AVATAR_ICON) {
				    		Utils.sendMessage(sender, "AvatarIcon: " + ChatColor.GREEN + "true");
				    	} else {
				    		Utils.sendMessage(sender, "AvatarIcon: " + ChatColor.RED + "false");
				    	}
				    	if (CM.ENABLE_FAKE_MAX_PLAYERS) {
				    		Utils.sendMessage(sender, "MaxPlayers: " + ChatColor.GREEN + "true");
				    	} else {
				    		Utils.sendMessage(sender, "MaxPlayers: " + ChatColor.RED + "false");
				    	}
					} else {
						Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd info");
					}
				}
    			//Update
    			else if (args[0].equalsIgnoreCase("update")) {
    				
    				if (!sender.hasPermission("sexymotd.update.perform")) {
    					Utils.sendError(sender, "You don't have permission!");
    					return true;
    				}
    				if (args.length <= 1) {
    					if (Main.updater_available) {
    						new Updater(Main.getInstance(), 76246, Main.file, Updater.UpdateType.NO_VERSION_CHECK, true);
    						Utils.sendMessage(sender, "SexyMotd is updated! It will be working upon restart!");
    					} else {
    						Utils.sendError(sender, "No update currently available!");
    					}
    				} else {
    					Utils.sendError(sender, "Usage: " + ChatColor.RED + "/bm update");
    				}
    				
    			}
    			//Check
    			else if (args[0].equalsIgnoreCase("check")) {
    				
    				if (!sender.hasPermission("sexymotd.update.check")) {
    					Utils.sendError(sender, "You don't have permission!");
    					return true;
    				}
    				if (args.length <= 1) {
						Main.checkUpdate();
						if (Main.updater_available) {
				        	Utils.sendMessage(sender, "A new update (" + Main.updater_name + ") is available!");
				        	Utils.sendMessage(sender, "Please type /bm update to update it automatically, or click the link below do download it manually:");
				        	Utils.sendMessage(sender, Main.updater_link);
						} else {
				        	Utils.sendMessage(sender, "Your SexyMotd is up to date!");
						}
    				} else {
    					sender.sendMessage(ChatColor.DARK_RED + "Usage: " + ChatColor.RED + "/bm check");
    				}
    			} 
    			else if (args[0].equalsIgnoreCase("reload")) {
    				if (!sender.hasPermission("sexymotd.reload")) {
    					Utils.sendError(sender, "Your do not have permission to reload!");
    					return true;
    				}
    				IpList.reload();
    				CM.reload();
    				sender.sendMessage(ChatColor.GREEN + "SexyMotd has reloaded!");
    			} 
    			else if (args[0].equalsIgnoreCase("restart")) {
    				if (!sender.hasPermission("sexymotd.restart")) {
    					Utils.sendError(sender, "You do not have permission to restart SexyMotd!");
    					return true;
    				}
    				Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
    				Main.getInstance().getServer().getPluginManager().enablePlugin(Main.getInstance());
    				sender.sendMessage(ChatColor.GREEN + "SexyMotd has restarted!");
    			}
    			else {
    				printHelp(sender);
    			}
			}
		}
		return false;
	}
	
	public static void printHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.DARK_AQUA + "===" + ChatColor.AQUA + " SexyMotd by the Pixelized Network " + ChatColor.DARK_AQUA + "===");
    	sender.sendMessage(ChatColor.DARK_GREEN + "Usage: " + ChatColor.GREEN + "/motd <params>");
		sender.sendMessage(ChatColor.YELLOW + "/motd help " + ChatColor.RED + "-" + ChatColor.RESET + " displays this help message");
		if (sender.hasPermission("sexymotd.toggle")) {
			sender.sendMessage(ChatColor.YELLOW + "/motd toggle " + ChatColor.RED + "-" + ChatColor.RESET + " toggles the plugin");
		}
		if (sender.hasPermission("sexymotd.motd.toggle")) {
			sender.sendMessage(ChatColor.YELLOW + "/motd togglemotd " + ChatColor.RED + "-" + ChatColor.RESET + " toggles the motd");
		}
		if (sender.hasPermission("sexymotd.motd.set")) {
			sender.sendMessage(ChatColor.YELLOW + "/motd setmotd <motd> " + ChatColor.RED + "-" + ChatColor.RESET + " sets the motd");
		}
		if (sender.hasPermission("sexymotd.motd.see")) {
			sender.sendMessage(ChatColor.YELLOW + "/motd seemotd " + ChatColor.RED + "-" + ChatColor.RESET + " displays the motd in chat");
		}
		if (sender.hasPermission("sexymotd.players.toggle")) {
			sender.sendMessage(ChatColor.YELLOW + "/motd players toggle " + ChatColor.RED + "-" + ChatColor.RESET + " toggles FakePlayers");
		}
		if (sender.hasPermission("sexymotd.players.toggle")) {
			sender.sendMessage(ChatColor.YELLOW + "/motd players toggle " + ChatColor.RED + "-" + ChatColor.RESET + " toggles FakePlayers");
		}
		if (sender.hasPermission("sexymotd.players.set")) {
			sender.sendMessage(ChatColor.YELLOW + "/motd players set <number> " + ChatColor.RED + "-" + ChatColor.RESET + " sets the FakePlayers");
		}
		if (sender.hasPermission("sexymotd.maxplayers.toggle")) {
			sender.sendMessage(ChatColor.YELLOW + "/motd maxplayers toggle " + ChatColor.RED + "-" + ChatColor.RESET + " toggles FakeMaxPlayers");
		}
		if (sender.hasPermission("sexymotd.maxplayers.set")) {
			sender.sendMessage(ChatColor.YELLOW + "/motd maxplayers set <number> " + ChatColor.RED + "-" + ChatColor.RESET + " sets the FakeMaxPlayers");
		}
		if (sender.hasPermission("sexymotd.avatericon")) {
			sender.sendMessage(ChatColor.YELLOW + "/motd avatericon " + ChatColor.RED + "-" + ChatColor.RESET + " toggles the AvatarIcon");
		}
		if (sender.hasPermission("sexymotd.update.perform")) {
			sender.sendMessage(ChatColor.YELLOW + "/motd update " + ChatColor.RED + "-" + ChatColor.RESET + " updates SexyMotd to the latest version");
		}
		if (sender.hasPermission("sexymotd.update.check")) {
			sender.sendMessage(ChatColor.YELLOW + "/motd check " + ChatColor.RED + "-" + ChatColor.RESET + " checks for updates");
		}
		if (sender.hasPermission("sexymotd.info")) {
			sender.sendMessage(ChatColor.YELLOW + "/motd info " + ChatColor.RED + "-" + ChatColor.RESET + " displays the info");
		}
		if (sender.hasPermission("sexymotd.reload")) {
			sender.sendMessage(ChatColor.YELLOW + "/motd reload" + ChatColor.RED + "-" + ChatColor.RESET + " reloads SexyMotd.");
		}
		if (sender.hasPermission("sexymotd.restart")) {
			sender.sendMessage(ChatColor.YELLOW + "/motd reload" + ChatColor.RED + "-" + ChatColor.RESET + " restarts SexyMotd.");
		}
	}
	
	public void sendPlayerCommandUsage(CommandSender sender) {
		Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd players toggle");
		Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd players add <number>");
		Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd players remove <index>");
		Utils.sendError(sender, "Usage: " + ChatColor.RED + "/motd players list");
	}
}
