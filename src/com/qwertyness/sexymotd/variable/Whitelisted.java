package com.qwertyness.sexymotd.variable;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.qwertyness.sexymotdengine.variable.Value;
import com.qwertyness.sexymotdengine.variable.Variable;

public class Whitelisted extends Variable {

	public Whitelisted() {
		super("whitelisted", VariableType.NORMAL);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getValue(String playerName, String ip) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
		if (player != null) {
			return new Boolean(player.isWhitelisted()).toString();
		}
		return new Boolean(false).toString();
	}

	@Override
	public Value handleOperators(String operatorString, String playerName, String ip) {
		return new Value(this.getValue(playerName, ip), "%" + this.name + "%");
	}
}
