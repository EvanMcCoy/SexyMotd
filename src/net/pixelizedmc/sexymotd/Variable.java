package net.pixelizedmc.sexymotd;

import org.bukkit.configuration.ConfigurationSection;

public class Variable {
	public String name;
	public String builtInVariable;
	public Operator operator;
	public String condition;
	public String value;
	public String negValue;

	public Variable(ConfigurationSection variable) {
		this.name = "%" + variable.getName() + "%";
		this.builtInVariable = variable.getString("variable").toLowerCase();
		this.operator = Operator.getOperatorFromSymbol(variable.getString("operator"));
		this.condition = variable.getString("condition");
		this.value = variable.getString("value");
		this.negValue = variable.getString("negValue");
	}
	
	public enum Operator {
		EQUAL, DOES_NOT_EQUAL, LESS_THAN, GREATER_THAN, LESS_THAN_OR_EQUAL_TO, GREATER_THAN_OR_EQUAL_TO;
		
		public static Operator getOperatorFromSymbol(String symbol) {
			if (symbol.contains(">=")) {
				return Operator.GREATER_THAN_OR_EQUAL_TO;
			}
			else if (symbol.contains("<=")) {
				return Operator.LESS_THAN_OR_EQUAL_TO;
			}
			if (symbol.contains(">")) {
				return Operator.GREATER_THAN;
			}
			else if (symbol.contains("<")) {
				return Operator.LESS_THAN;
			}
			else if (symbol.contains("!=")) {
				return Operator.DOES_NOT_EQUAL;
			}
			else {
				return Operator.EQUAL;
			}
		}
	}
}
