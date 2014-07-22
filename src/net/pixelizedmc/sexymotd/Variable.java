package net.pixelizedmc.sexymotd;

import org.bukkit.configuration.ConfigurationSection;

public class Variable {
	public String name;
	public String builtInVariable;
	public Operator operator;
	public String condition;
	public String value;

	public Variable(ConfigurationSection variable) {
		this.name = "%" + variable.getName() + "%";
		this.builtInVariable = variable.getString("variable").toLowerCase();
		this.operator = Operator.getOperatorFromSymbol(variable.getString("operator"));
		this.condition = variable.getString("condition");
		this.value = variable.getString("value");
	}
	
	public enum Operator {
		EQUAL, LESS_THAN, GREATER_THAN;
		
		public static Operator getOperatorFromSymbol(String symbol) {
			if (symbol.contains(">")) {
				return Operator.GREATER_THAN;
			}
			else if (symbol.contains("<")) {
				return Operator.LESS_THAN;
			}
			else {
				return Operator.EQUAL;
			}
		}
	}
}
